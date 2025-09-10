<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
    Chat Nhóm Bằng RMI
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

## 1. Giới thiệu hệ thống
Hệ thống Chat Nhóm bằng RMI được xây dựng nhằm hỗ trợ nhiều người dùng có thể giao tiếp trực tuyến với nhau thông qua công nghệ **Java RMI (Remote Method Invocation)**.  
Ứng dụng cho phép nhiều client cùng kết nối đến server và thực hiện gửi, nhận tin nhắn trong thời gian thực.  

**Các chức năng chính:**
- Người dùng (Client):
  - Kết nối tới server chat
  - Gửi tin nhắn đến nhóm
  - Nhận tin nhắn từ các thành viên khác
- Quản trị viên (Server):
  - Quản lý danh sách client đang kết nối
  - Phân phối tin nhắn đến tất cả client trong nhóm

