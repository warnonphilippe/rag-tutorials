# Rag with DeepSeek and Ollama
This is a sample RAG System with DeepSeek and Ollama. 

## Prerequisites
Before starting this tutorial, you need to have the following prerequisites:
- Java Development Kit (JDK) 21 or higher
- Maven 3.6.3 or higher
- Basic knowledge of Java
- Basic knowledge of LangChain4j
- Docker 
- Ollama

## Building the project
To build the project, you need to follow these steps:
1. Clone the repository
2. Go to the `rag-with-deepseek-ollama` directory
3. Run the following command:
```bash
mvn clean install
```
4. The project will be built and the JAR file will be created in the `target` directory
5. You can run the JAR file using the following command:
```bash
java -jar target/rag-with-deepseek-ollama-1.0-SNAPSHOT.jar
```
## Environment Configuration
The system requires the following environment variables:
- `OLLAMA_URL`: the URL of the Ollama service
