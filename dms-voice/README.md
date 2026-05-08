# DMS Voice Assistant

A voice-controlled assistant for the DMS (Direct Messaging System) that supports speech recognition and natural language processing in both English and Turkish.

## Features

- **Speech Recognition**: Uses Vosk for offline speech recognition
- **Multilingual Support**: English and Turkish language support
- **Voice Commands**:
  - Send messages to contacts or groups
  - Search messages and archives
  - Clear conversations
  - Switch audio on/off
  - Archive or delete messages
  - Logout

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Vosk speech recognition models (download separately)

## Dependencies

- `io.github.dmspack:dms-core:2.0.0` - DMS core functionality
- `com.alphacephei:vosk:0.3.45` - Speech recognition
- `com.fasterxml.jackson.core:jackson-databind:2.21.3` - JSON processing
- `org.slf4j:slf4j-simple:2.0.9` - Logging

## Building

```bash
mvn clean compile
```

## Running

```bash
java -cp target/classes com.onurg.dmsvoice.VoiceAssistantApp <username> <password> <model-path>
```

### Parameters

- `username`: DMS username
- `password`: DMS password
- `model-path`: Path to Vosk model directory (e.g., `vosk-model-en-us-0.22`)

### Vosk Models

Download models from https://alphacephei.com/vosk/models:

- English: `vosk-model-en-us-0.22`
- Turkish: `vosk-model-tr-0.3`

## Voice Commands

### English

- "Send message to John saying hello" - Send message to contact
- "Tell Sarah I will be late" - Send message to contact
- "Search for meeting notes" - Search messages
- "Search archive for project files" - Search archived messages
- "Clear conversation" - Clear current conversation
- "Turn audio on/off" - Toggle audio
- "Archive messages 1,2,3" - Archive specific messages
- "Delete messages 4,5" - Delete specific messages
- "Logout" - Logout from DMS

### Turkish

- "Ahmete merhaba de" - Send message to contact
- "Ayşe'ye geç kalacağım söyle" - Send message to contact
- "Toplantı notlarını ara" - Search messages
- "Arşivde proje dosyalarını ara" - Search archived messages
- "Sohbeti temizle" - Clear current conversation
- "Sesi aç/kapat" - Toggle audio
- "Mesajları arşivle 1,2,3" - Archive specific messages
- "Mesajları sil 4,5" - Delete specific messages
- "Çıkış" - Logout from DMS

## Architecture

- `VoiceAssistantApp`: Main application entry point
- `DmsVoiceAssistant`: Core voice assistant logic
- `IntentParser`: Natural language processing for command recognition
- `VoskSpeechRecognizer`: Speech recognition wrapper
- `DmsControlAdapter`: Interface to DMS functionality
- `VoiceCommand`: Data class for parsed commands
- `VoiceIntent`: Enumeration of supported intents

## Testing

```bash
mvn test
```

## Notes

- Speech recognition works offline using Vosk models
- Message IDs for archive/delete should be comma-separated numbers
- The assistant continuously listens until "logout" command is given
- Audio switching controls DMS audio notifications</content>
<parameter name="filePath">d:/dev/local/TESTWS/dms-voice/README.md