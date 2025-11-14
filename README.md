ISO8583 POS Simulator

A fully open-source Android project that simulates a real POS (Point of Sale) terminal using the ISO8583 standard.
This project demonstrates how payment terminals communicate with a host server using ISO8583 messages, 3DES encryption, PIN Block generation, and TCP/IP networking.

This simulator is designed for:

Developers who want to learn ISO8583

People who want to understand POS-to-Host communication

Security & fintech enthusiasts

Students working on payment systems

Anyone looking for a realistic ISO8583 Android implementation

Project Purpose

This is NOT a banking or financial system.
It is a safe educational emulator that recreates the flow of a real POS device:

Enter amount

Enter PIN

Generate PIN Block

Build ISO8583 message (MTI 0200)

Send request to a test server

Receive 0210 response

Display transaction result

All operations use test data, mock keys, and no real PAN/PIN information.

Tech Stack

Android + Kotlin

Jetpack Compose

Clean Architecture

MVI

DES & 3DES Encryption

ISO8583 Message Builder

TCP/IP Socket Client

Server Emulator (separate module)

/android-client     → POS terminal simulator  
/server-emulator    → ISO8583 host test server  
/docs               → Technical documentation  


Security Notice

This project is for learning purposes only.
Do NOT use it for real financial transactions.
