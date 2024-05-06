# Secure Chat
Secure Chat is a Java-based chatting application designed to ensure secure communication over sockets. It incorporates several cryptographic techniques to guarantee confidentiality, authenticity, and integrity of messages exchanged between clients.

## Features
- **Diffie-Hellman Key Exchange:** Securely establishes a shared secret key between clients and the server.
- **ElGamal Digital Signature:** Provides message authentication to ensure the validity of key exchange messages.
- **AES Encryption:** Encrypts chat messages using the shared secret key, ensuring confidentiality.

## How to Run
To run the Secure Chat application, follow these steps:

1. **Clone the Repository:** Clone this repository to your local machine using Git:

   ```bash
   git clone https://github.com/itsHamdySalem/Secure-Chat.git
   ```

2. **Navigate to the Project Directory:** Open a terminal and navigate to the root directory of the project:

   ```bash
   cd Secure-Chat
   ```

3. **Start the Server:** Open a terminal and run the Server class:

   ```bash
   java .\src\socket\Server.java
   ```

4. **Start the Clients:** Open two separate terminals and run the Client class in each terminal:

   ```bash
   java .\src\socket\Client.java
   ```

5. **Start Chatting:** Once both clients are connected to the server, you can start chatting securely!

## Dependencies
- Java Development Kit (JDK) 8 or later
- Git (for cloning the repository)

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
