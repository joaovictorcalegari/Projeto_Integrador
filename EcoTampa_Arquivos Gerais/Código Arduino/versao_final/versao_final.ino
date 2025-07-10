/*
  ===================================================================
  ==  PROJETO COLETOR DE TAMPINHAS - VERSÃO COM ESTEIRA (TB6600)  ==
  ==  Código Final v6: Arquitetura Dual-Core para movimento fluido ==
  ==  com Firebase.                                                 ==
  ===================================================================
*/

// --- BIBLIOTECAS ---
#include <Arduino.h>
#include "HX711.h"
#include <AccelStepper.h>
#include <WiFi.h>
#include "IOXhop_FirebaseESP32.h"

// --- WIFI/FIREBASE ---
#define WIFI_SSID "bolda"
#define WIFI_PASSWORD "opiq12345"
#define FIREBASE_HOST "https://ecotampa-110c8-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "AIzaSyCj-frjgz6Kl-36HqY4pb_JzfDXHtwFSwY"

// --- PINOS ---
const int PINO_HX711_DT = 16;
const int PINO_HX711_SCK = 4;
const int PINO_SENSOR_IR = 33;
const int PINO_MOTOR_STEP = 25;
const int PINO_MOTOR_DIR = 27;
const int PINO_MOTOR_ENABLE = 14;

// --- PARÂMETROS ---
const float FATOR_DE_CALIBRACAO = -275.316;
const float PESO_MAXIMO_GRAMAS = 1000.0;
const int DEBOUNCE_DELAY_MS = 200;
const int VELOCIDADE_ESTEIRA = 1500;
const int ACELERACAO_ESTEIRA = 300;

// --- OBJETOS GLOBAIS ---
HX711 scale;
AccelStepper esteira(AccelStepper::DRIVER, PINO_MOTOR_STEP, PINO_MOTOR_DIR);

// --- MÁQUINA DE ESTADOS ---
enum EstadoProjetor { AGUARDANDO, PREPARANDO_TARA, TARING, CONTANDO, CHEIO, FINALIZANDO };
volatile EstadoProjetor estadoAtual = AGUARDANDO; // Volatile pois será acessada por dois núcleos

// --- VARIÁVEIS GLOBAIS ---
unsigned long ultimoCheckComando = 0;
const long INTERVALO_CHECK_COMANDO = 300;
volatile int contadorTampinhas = 0; // Volatile
volatile unsigned long ultimoTempoDeteccao = 0; // Volatile
volatile float pesoAtual = 0; // Volatile
unsigned long tempoInicioPausa = 0;

// ===================================================================
// --- TAREFA PARA O NÚCLEO 0 (Sensores e Firebase) ---
// ===================================================================
void TarefasSecundarias(void *pvParameters) {
  Serial.println("Tarefa secundaria (Sensores/Firebase/Comandos) iniciada no Nucleo 0.");

  unsigned long ultimoTempoPeso = 0;
  unsigned long ultimoReporte = 0;
  unsigned long ultimoCheckComando = 0;

  const long INTERVALO_LEITURA_PESO = 250;
  const long INTERVALO_REPORTE = 2000;
  const long INTERVALO_CHECK_COMANDO = 1500;

  for (;;) {
    // --- VERIFICAR COMANDO ON/OFF ---
    if ((estadoAtual == AGUARDANDO || estadoAtual == CONTANDO) &&
        (millis() - ultimoCheckComando >= INTERVALO_CHECK_COMANDO)) {

      ultimoCheckComando = millis();
      Serial.println("[NUCLEO 0] Checando comando no Firebase...");

      bool comandoRecebido = Firebase.getBool("controle/iniciarEsteira");

      if (Firebase.failed()) {
        Serial.println("[NUCLEO 0] Erro ao obter controle/iniciarEsteira.");
      } else {
        if (comandoRecebido && estadoAtual == AGUARDANDO) {
          Serial.println("[NUCLEO 0] COMANDO RECEBIDO: INICIAR ESTEIRA");
          estadoAtual = PREPARANDO_TARA;

        } else if (!comandoRecebido && estadoAtual == CONTANDO) {
          Serial.println("[NUCLEO 0] COMANDO RECEBIDO: PARAR ESTEIRA");
          esteira.stop();
          esteira.disableOutputs();
          estadoAtual = AGUARDANDO;
        }
      }
    }

    // --- REPORTE DE STATUS E PESO (somente durante a contagem) ---
    if (estadoAtual == CONTANDO) {
      if (millis() - ultimoTempoPeso >= INTERVALO_LEITURA_PESO) {
        ultimoTempoPeso = millis();

        if (scale.is_ready()) {
          pesoAtual = scale.get_units(2);
          if (pesoAtual < 0) pesoAtual = 0;
          if (pesoAtual >= PESO_MAXIMO_GRAMAS) {
            estadoAtual = CHEIO;
          }
        }
      }

      if (millis() - ultimoReporte >= INTERVALO_REPORTE) {
        ultimoReporte = millis();

        Serial.print("[NUCLEO 0] Tampinhas: ");
        Serial.print(contadorTampinhas);
        Serial.print(" | Peso: ");
        Serial.print(pesoAtual, 1);
        Serial.println(" g");

        Firebase.setInt("tampas/count", contadorTampinhas);
        if (Firebase.failed()) {
          Serial.println("[ERRO] Falha ao enviar contagem ao Firebase.");
        }
      }
    }

    // <<<====================== BLOCO ALTERADO ======================>>>
    // --- SINCRONIZAR contadorTampinhas com Firebase (SOMENTE NO MODO DE ESPERA) ---
    if (estadoAtual == AGUARDANDO) {
      int valorFirebase = Firebase.getInt("tampas/count");
      if (!Firebase.failed() && valorFirebase == 0 && contadorTampinhas != 0) {
        Serial.println("[NUCLEO 0] Firebase foi zerado, zerando contador local.");
        contadorTampinhas = 0;
      }
    }
    // <<<==============================================================>>>

    delay(100); // Evita sobrecarregar o núcleo
  }
}

// --- ROTINA DE INTERRUPÇÃO (ISR) ---
void IRAM_ATTR contarTampinhaISR() {
  unsigned long agora = millis();
  if (agora - ultimoTempoDeteccao > DEBOUNCE_DELAY_MS) {
    contadorTampinhas++;
    ultimoTempoDeteccao = agora;
  }
}

// ===================================================================
// ---           SETUP - Roda no Núcleo 1                          ---
// ===================================================================
void setup() {
  Serial.begin(115200);
  Serial.println("\n\nColetor de Tampinhas - v6 Dual-Core - Sistema Iniciado");

  // --- CONEXÃO WIFI ---
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Conectando ao WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println("\nConectado! IP: " + WiFi.localIP().toString());
  Firebase.setBool("status/wifiConectado", true);

  // --- FIREBASE ---
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  // Garante que o comando no Firebase comece como 'false'
  Firebase.setBool("controle/iniciarEsteira", false);

  Serial.println("\nSistema pronto. Em modo AGUARDANDO.");

  // --- CRIAÇÃO DA TAREFA SECUNDÁRIA NO NÚCLEO 0 ---
  xTaskCreatePinnedToCore(
    TarefasSecundarias,   // Função que a tarefa vai executar
    "TarefasSecundarias", // Nome da tarefa
    10000,                // Tamanho da pilha (stack size)
    NULL,                 // Parâmetros da tarefa
    1,                    // Prioridade da tarefa
    NULL,                 // Handle da tarefa
    0                     // Núcleo onde a tarefa vai rodar (0)
  );

  // --- SETUP DA ESTEIRA ---
  esteira.setEnablePin(PINO_MOTOR_ENABLE);
  esteira.setPinsInverted(true, false, true);
  esteira.setMaxSpeed(1500);
  esteira.setAcceleration(ACELERACAO_ESTEIRA);
  esteira.setSpeed(0);
  esteira.disableOutputs();
  Serial.println("Esteira inicializada corretamente.");

  // --- SETUP DA BALANÇA E SENSOR IR ---
  scale.begin(PINO_HX711_DT, PINO_HX711_SCK);
  scale.set_scale(FATOR_DE_CALIBRACAO);
  pinMode(PINO_SENSOR_IR, INPUT);
  attachInterrupt(digitalPinToInterrupt(PINO_SENSOR_IR), contarTampinhaISR, FALLING);

  Serial.println("\nSistema pronto. Em modo AGUARDANDO.");
}

// ===================================================================
// ---           LOOP PRINCIPAL - Roda no Núcleo 1                 ---
// ===================================================================
void loop() {
  static unsigned long ultimaVerificacao = 0;
  if (millis() - ultimaVerificacao >= 5000) { // a cada 5s
    ultimaVerificacao = millis();
    bool conectado = WiFi.status() == WL_CONNECTED;
    Firebase.setBool("status/wifiConectado", conectado);
  }
  switch (estadoAtual) {
    case AGUARDANDO:
      if (Serial.available() > 0) {
        String input = Serial.readStringUntil('\n');
        input.trim();
        if (input == "c") {
          Serial.println("\n[!] Comando 'c' reconhecido.");
          estadoAtual = PREPARANDO_TARA;
        }
      }
      break;

    case PREPARANDO_TARA:
      Serial.println("[!] Esvazie o recipiente. Zerando a balanca em 3 segundos...");
      tempoInicioPausa = millis();
      estadoAtual = TARING;
      break;

    case TARING:
      if (millis() - tempoInicioPausa >= 3000) {
        contadorTampinhas = 0;
        pesoAtual = 0;
        scale.tare();
        Serial.println("[!] Tara concluida. Balanca zerada.");
        Serial.println("[!] LIGANDO A ESTEIRA. Pode inserir as tampinhas.");
        esteira.enableOutputs();
        esteira.setSpeed(VELOCIDADE_ESTEIRA);
        estadoAtual = CONTANDO;
      }
      break;

    case CONTANDO:
      esteira.runSpeed();
      break;

    case CHEIO:
      Serial.println("\n[!] PESO MAXIMO ATINGIDO!");
      esteira.stop();
      esteira.disableOutputs();
      Serial.println("... (impressão final e envio ao Firebase ocorrerão no próximo ciclo de reporte do nucleo 0)");
      tempoInicioPausa = millis();
      estadoAtual = FINALIZANDO;
      break;

    case FINALIZANDO:
      if (millis() - tempoInicioPausa >= 2500) {
        Serial.println("Sistema voltando para o modo AGUARDANDO.");
        estadoAtual = AGUARDANDO;
      }
      break;
  }
}