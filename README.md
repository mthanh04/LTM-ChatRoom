<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
    CHAT NHÓM SỬ DỤNG GIAO THỨC RMI
</h2>
<div align="center">
    <p align="center">
        <img alt="AIoTLab Logo" width="170" src="docs/aiotlab_logo.png" />
        <img alt="DaiNam University Logo" width="200" src="docs/fitdnu_logo.png" />
        <img alt="CNTT Logo" width="180" src="docs/dnu_logo.png" />
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>


---

## 📖 1. Giới thiệu hệ thống
Ứng dụng **Chat Nhóm sử dụng giao thức RMI** cho phép nhiều người dùng giao tiếp trực tuyến thời gian thực thông qua công nghệ **Java RMI (Remote Method Invocation)**.  

**Server**: đóng vai trò trung tâm, quản lý danh sách client và phân phối tin nhắn.  
**Client**: cung cấp giao diện trực quan để gửi và nhận tin nhắn.  

**Các chức năng chính:**
- **Server**:
  - Quản lý danh sách client kết nối
  - Phát tin nhắn đến toàn bộ client
  - Gửi thông báo hệ thống (người tham gia/rời khỏi)
- **Client**:
  - Kết nối đến server
  - Gửi và nhận tin nhắn
  - Hiển thị thông báo hệ thống
  - Giao diện trực quan bằng Swing

---

## 🔧 2. Công nghệ sử dụng
#### Java RMI (Remote Method Invocation)  
- Cung cấp cơ chế gọi phương thức từ xa giữa client và server.  
- Sử dụng interface `Remote` và các lớp `UnicastRemoteObject`, `Registry`.  

#### Java Swing  
- Xây dựng giao diện người dùng (GUI) với các thành phần:  
  - `JFrame`: cửa sổ chính  
  - `JTextArea`: hiển thị nội dung chat  
  - `JTextField`: nhập tin nhắn  
  - `JButton`: gửi tin nhắn  
  - `JScrollPane`: hỗ trợ cuộn lịch sử chat  

---

## 🚀 3. Hình ảnh các chức năng

<p align="center">
  <img src="docs/chat_mess.png" alt="Server UI" width="700"/>
</p>
<p align="center">
  <em>Hình 1: Giao diện chat chính của các client</em>
</p>

<p align="center">
  <img src="docs/mess.png" alt="Client UI" width="500"/>
</p>
<p align="center">
  <em>Hình 2: Giao diện Client chat nhóm</em>
</p>

<p align="center">
  <img src="docs/mess.png" alt="Message Broadcast" width="700"/>
</p>
<p align="center">
  <em>Hình 3: Thông báo khi có client rời khỏi nhóm chat</em>
</p>

---

## 📝 4. Hướng dẫn cài đặt và sử dụng

### 🔧 Yêu cầu hệ thống
- **Java Development Kit (JDK)**: Phiên bản 11 trở lên  
- **Hệ điều hành**: Windows / macOS / Linux  
- **IDE khuyến nghị**: IntelliJ IDEA, Eclipse, hoặc NetBeans  
- **Bộ nhớ**: Tối thiểu 512MB RAM  

---

### 📦 Các bước triển khai

#### 🔹 Bước 1: Chuẩn bị môi trường
1. **Cài đặt JDK** nếu chưa có:  
   - Kiểm tra bằng lệnh:  
     ```bash
     java -version
     javac -version
     ```
   - Nếu chưa có, tải JDK tại [Oracle JDK](https://www.oracle.com/java/technologies/javase-downloads.html) hoặc [OpenJDK](https://adoptium.net/).

2. **Tải mã nguồn dự án**:  
   - Clone repo bằng Git:  
     ```bash
     git clone https://github.com/mthanh04/LTM-ChatRoom
     ```
   - Hoặc tải file `.zip` và giải nén.

---

#### 🔹 Bước 2: Biên dịch mã nguồn
Di chuyển đến thư mục `bin` rồi biên dịch:  
```bash
cd BOXCHAT/bin
rmiregistry
```
#### 🔹 Bước 3: Chạy file ChatServer.java

#### 🔹 Bước 4: Chạy file ChatClient.java
- Giao diện chat sẽ hiện ra

## 👤 5. Liên hệ
**Họ tên**: Trịnh Minh Thành.  
**Lớp**: CNTT 16-03.  
**Email**: thanhmeo260604@gmail.com.

© 2025 Faculty of Information Technology, DaiNam University. All rights reserved.



