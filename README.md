\# Java Secure Messaging App



A secure, peer‑to‑peer chat application written in Java with metadata obfuscation techniques to protect against traffic analysis. Inspired by privacy tools like Signal, Tor, and Mixnets, this project implements strong encryption and traffic shaping to improve communication privacy.



\## 🛠 Features



\- \*\*End‑to‑end encryption\*\*

&nbsp; - RSA (or ECC) for key exchange

&nbsp; - AES‑256‑GCM for fast, authenticated message encryption

\- \*\*Metadata obfuscation\*\*

&nbsp; - Constant‑rate dummy traffic

&nbsp; - Random timing jitter between sends

&nbsp; - Anonymous session identifiers

\- \*\*Networking\*\*

&nbsp; - Peer‑to‑peer messaging over TCP sockets

&nbsp; - Fixed‑size encrypted packets to reduce metadata leakage

\- \*\*Message storage\*\*

&nbsp; - Secure, encrypted SQLite database for message history

\- \*\*Optional GUI\*\*

&nbsp; - Built using Swing (or JavaFX) for a simple chat window



\## 🚀 Quick Start



\### Requirements



\- Java 8 or newer

\- SQLite JDBC Driver (added to classpath)

\- Git (for cloning and building)



\### Running



1\. Clone the repository:



&nbsp;  ```bash

&nbsp;  git clone https://github.com/George-Pontsis/Java\_Secure\_Messaging\_App.git

&nbsp;  cd Java\_Secure\_Messaging\_App



