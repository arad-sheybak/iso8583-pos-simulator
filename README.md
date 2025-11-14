# 🛠 ISO8583 POS Simulator

[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-blue?logo=kotlin)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-100%25-green?logo=android)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Stars](https://img.shields.io/github/stars/arad-sheybak/iso8583-pos-simulator?style=social)](https://github.com/arad-sheybak/iso8583-pos-simulator/stargazers)

A **fully open-source Android POS terminal simulator** using the **ISO8583 standard**.  
Learn how payment terminals communicate with a host server using **ISO8583 messages**, **3DES encryption**, **PIN Block generation**, and **TCP/IP networking**.

<p align="center">
<img src="./images/img_iso8583.png" alt="POS Simulator Demo" width="512"/>
</p>


## 🚀 Features

- Simulate a **real POS transaction flow**
- Enter amount and PIN
- Generate **PIN Block**
- Build **ISO8583 messages** (MTI 0200)
- Send requests to a **test server**
- Receive responses (MTI 0210)
- Display transaction results
- **Safe**: Uses test data and mock keys, no real PAN/PIN

---

## 🎯 Who Is This For?

- Developers wanting to learn **ISO8583**
- Security & fintech enthusiasts
- Students working on **payment systems**
- Anyone looking for a realistic **ISO8583 Android implementation**

---

## ⚠️ Important Notice

> This is **NOT** a banking or financial system.  
> It is a safe **educational emulator** designed for learning purposes only.  
> Do **NOT** use it for real financial transactions.

---

## 🖥 Project Structure

    iso8583-pos-simulator
    ├── android-client → POS terminal simulator
    ├── server-emulator → ISO8583 host test server
    └── docs → Technical documentation


---

## 🛠 Tech Stack

- **Android + Kotlin**
- **Jetpack Compose**
- **Clean Architecture**
- **MVI Pattern**
- **DES & 3DES Encryption**
- **ISO8583 Message Builder**
- **TCP/IP Socket Client**
- Server Emulator (separate module)

---

## 📚 Transaction Flow

1. Enter **amount**
2. Enter **PIN**
3. Generate **PIN Block**
4. Build **ISO8583 message (MTI 0200)**
5. Send request to a **test server**
6. Receive **0210 response**
7. Display **transaction result**

---

## 🎬 Demo / Screenshots

![POS Simulator Demo](https://media.giphy.com/media/your-demo-gif.gif)

*(there is no screen shot now, but in the future we will add them here)*

---

## 🔗 Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/iso8583-pos-simulator.git
```
2. Open in Android Studio

3. Run the android-client module on an emulator or device

4. Run the server-emulator module to simulate host responses

---
## ⭐ Why Star This Project?

If you find this project helpful or educational, give it a ⭐!
It helps the community and motivates continued development.

- **Learn ISO8583 from scratch**

- **Understand POS-to-host communication**

- **Hands-on security & encryption experience**

- **Fully open-source, educational, and safe**
---
## 💡 Contributing

Contributions, issues, and feature requests are welcome!

## 📄 License

This project is licensed under the MIT License 