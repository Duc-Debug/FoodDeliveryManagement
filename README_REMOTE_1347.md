# Food Delivery Management System (Java SE Console)
Tài liệu này hướng dẫn cách sử dụng ứng dụng Console giao đồ ăn. Ứng dụng hỗ trợ tối ưu quy trình điều hành thực đơn, duyệt đơn và quản lý doanh thu.

## 1. Mục tiêu dự án

- Làm template chuẩn kiến trúc phân tầng (Layered Architecture).
- Rèn luyện OOP nâng cao: kế thừa, đa hình, interface, abstract class, generic, exception, tách lớp trách nhiệm.
- Thực hành mở rộng nghiệp vụ theo hướng clean code và dễ bảo trì.
  
## 2. Yêu cầu môi trường

- JDK 17 trở lên.
- Terminal (zsh/bash/cmd) để biên dịch và chạy thủ công.
- IDE khuyến nghị: VS Code hoặc IntelliJ.

Kiểm tra Java:

    java -version
    javac -version

## 3. Cấu trúc thư mục

    ├── data/
    │   ├── menu.csv
    │   ├── orders.csv
    │   ├── store.csv
    │   └── users.csv
    └── src/main/java/com/delivery/
    ├── config/
    │   └── StoreConfig.java
    ├── exception/
    │   ├── BaseDeliveryException.java
    │   ├── InvalidStateException.java
    │   ├── NotFoundException.java
    │   └── ValidationException.java
    ├── model/
    │   ├── Cart.java
    │   ├── Drink.java
    │   ├── Food.java
    │   ├── MenuItem.java
    │   ├── Order.java
    │   ├── OrderItem.java
    │   ├── OrderState.java
    │   └── User.java
    ├── repository/
    │   ├── GenericsRepository.java
    │   └── IRepository.java
    ├── services/
    │   ├── strategy/
    │   ├── IMenuService.java
    │   ├── IOrderService.java
    │   ├── IUserService.java
    │   ├── MenuServiceImpl.java
    │   ├── OrderServiceImpl.java
    │   └── UserServiceImpl.java
    ├── ui/
    │   ├── AppConsole.java
    │   └── Main.java
    └── util/
        ├── CsvMapper.java
        └── FileHandler.java
## 4. Vai trò từng tầng

### 4.1 Tầng Model (Domain)

- Chứa các đối tượng nghiệp vụ cốt lõi: User, MenuItem, Food, Drink, Order, OrderItem, Cart.
- MenuItem là abstract class bắt buộc lớp con phải override getDetailDescription().
- OrderState là enums giúp lưu trữ các trạng thái của một đơn hàng.
- User lưu trữ thông tin cá nhân của khách hàng.
- Cart lưu trữ các món ăn tạm thời chưa được đặt.

### 4.2 Tầng Repository (Data Access)

- Chuẩn hóa CRUD qua generic interface IRepository<T, ID>.
- GenericRepository<T, ID> triển khai lưu trữ tạm bằng Map trong RAM.
- Có thể thay thế bằng repository đọc/ghi file hoặc database trong tương lai mà không đổi logic ở service.

### 4.3 Tầng Service (Business)

- Chứa luật nghiệp vụ:
  - kiểm tra đăng ký tài khoản mới,
  - thực thi nghiệp vụ nạp/rút,
  - kiểm tra thông tin tạo món,
  - kiểm tra, chặn xóa món nếu món đang được tạo.
  - tính giá bán cuối cùng,
  - kiểm tra trạng thái đơn hàng,
  - trừ tiền khi checkout. cộng tiền cho chủ quán,
  - đánh giá các đơn hàng đã hoàn thành,
- Là tầng trung gian giữa UI và Repository.

### 4.4 Tầng Exception (Error Handling)

- Chuẩn hóa lỗi nghiệp vụ riêng của hệ thống, tránh ném Exception chung chung.
- Dùng BaseStoreException làm exception cha.

### 4.5 Tầng Util (Hỗ trợ kỹ thuật)

- FileHandler chứa phương thức generic để đọc/ghi text/csv.
- CsvMapper chứa phương thức chuyển đổi (Mapping) các đối tượng RAM sang chuỗi CSV phẳng.Gom toàn bộ logic xử lý chuỗi bẩn về một mối.
- Tách khỏi business để tái sử dụng và test dễ hơn.

### 4.6 Tầng UI (Presentation)

- AppConsole hiển thị menu, đọc input và gọi service.
- Main thực hiện manual dependency injection.

## 5. Luồng chạy tổng quát

1. Main khởi tạo repository in-memory cho User, Menu và Order.
2. Main khởi tạo UserServiceImpl, MenuServiceImpl và OrderServiceImpl.
3. Main truyền service vào AppConsole.
4. AppConsole chạy vòng lặp menu, nhận lựa chọn người dùng.
5. Service xử lý nghiệp vụ và gọi repository để CRUD.

## 6. Cách biên dịch và chạy

Từ thư mục gốc dự án:

Biên dịch:

    javac $(find src/main/java -name "*.java")

Chạy ứng dụng:

    java -cp src/main/java com.delivery.ui.Main

Gợi ý dọn file class sau khi thử:

    find src/main/java -name "*.class" -delete

## 7. Hướng dẫn sử dụng menu Console
Menu hiện có:

  Menu khởi động:
  - 1. Access CUSTOMER subsystem
  - 2. Access Restaurant Admin subsystem
  - 3. Create a new account
  - 4. Exit system
  - 0. Exit system

  Menu Customer:
  - 1. Deposit money into wallet
  - 2. Add food to draft cart
  - 3. View Order & Proceed to checkout
  - 4. Write a review / Rate an order
  - 5. Order history
  - 0. Log out (Return to main menu)

  Menu Admin:
  - 1. View Store Revenue (Xem doanh thu)
  - 2. Manage Menu
  - 3. Manage Orders
  - 4. Manage Promotions
  - 0. Logout
  
Lưu ý: tính năng Manage Promotions mới chỉ có ý tưởng chưa hiện thực hóa nó.
## 8. Tài liệu tính năng Thêm mới Sản phẩm

### 8.1 Mục tiêu tính năng

- Cho phép người dùng thêm mới sản phẩm ẩm thực từ Console theo 2 loại đa hình:
  - Đồ ăn (Food) 
  - Đồ uống (Drink) 
- Đảm bảo dữ liệu đầu vào hợp lệ trước khi lưu trữ.
- Tuân thủ đúng luồng Layered Architecture: UI -> Service -> Repository.

### 8.2 Các lớp tham gia

- UI:
  - `AdminConsole.manageMenu()`
  - Các hàm nhập liệu an toàn: `readStringInput()`, `readIntInput()`, `newLine()`
- Service:
  - `MenuServiceImpl.createMenu(MenuItem item)`
  - `MenuServiceImpl.validateCanCreate(MenuItem item)`
- Repository:
  - `InMemoryRepository<MenuItem, String>.create(MenuItem entity)`
- Util / Config:
  - `FileHandler.writeToCsv(Path path, List<T> data, Function<T, String> csvMapper)` 
  - `CsvMapper.toCsvRow(MenuItem item)` 

### 8.3 Luồng xử lý chi tiết

1. Admin đăng nhập thành công và chọn menu quản lý thực đơn (Manage Menu).
2. UI cung cấp lựa chọn thêm mới: Add New Food (Đồ ăn) hoặc Add New Drink (Đồ uống).
3. UI đọc các trường thông tin chung của sản phẩm ẩm thực: `id` (mã món), `name` (tên món), `basePrice` (giá bán gốc), `description` (mô tả ngắn).
4. UI đọc các trường thông tin riêng biệt tùy theo phân loại ẩm thực được chọn:
  - **Đồ ăn (Food):** `portionSize` (định lượng suất ăn) và `isVegetarian` (món chay hay mặn).
  - **Đồ uống (Drink):** `size` (kích cỡ mặc định), `defaultSugarLevel` (mức đường mặc định), `defaultIceLevel` (mức đá mặc định).
5. UI khởi tạo đối tượng `Food` hoặc `Drink` tương ứng và chuyển xuống tầng nghiệp vụ bằng cách gọi `menuService.createMenu(item)`.
6. Service thực hiện kiểm tra tính hợp lệ về mặt dữ liệu (Validate Can Create):
  - Đối tượng `item` truyền vào không được phép null.
  - Các trường bắt buộc như `id`, `name`, `description` không được phép trống hoặc chứa toàn khoảng trắng.
  - Thuộc tính giá bán gốc (`basePrice`) bắt buộc phải là số thực lớn hơn 0. Nếu không thỏa mãn, hệ thống ném ra `ValidationException` (Mã lỗi: `INVALID_INPUT`).
7. Service tiếp tục gọi xuống tầng lưu trữ `menuRepository.create(item)`.
8. Tầng Repository thực hiện kiểm tra trùng khóa định danh `id` trong bộ nhớ RAM:
  - Nếu phát hiện `id` đã tồn tại, hệ thống chặn đứng hành vi và ném ra ngoại lệ `ValidationException` (Mã lỗi: `DUPLICATE_ID`).
  - Nếu hợp lệ, đưa sản phẩm ẩm thực mới vào lưu trữ tạm thời trên RAM.
9. Sau khi tạo thành công trên RAM, Service gọi `saveToFile()` để ghi đè toàn bộ dữ liệu thực đơn mới nhất xuống file cơ sở dữ liệu `data/menu.csv`.
10. Nếu xảy ra lỗi vật lý khi ghi file (`IOException`):
  - Khối catch của Service sẽ tự động thực hiện cơ chế hoàn tác (Rollback): Gọi `menuRepository.delete(item.getId())` để xóa sạch bản ghi nháp vừa tạo ra khỏi bộ nhớ RAM.
  - Ném ngược ngoại lệ ra ngoài tầng UI kèm thông báo lỗi cụ thể để tránh tình trạng lệch dữ liệu giữa RAM và File cứng.
11. UI bắt lấy ngoại lệ, hiển thị thông báo lỗi hoặc in thông báo tạo sản phẩm ẩm thực thành công ra màn hình Console.

### 8.4 Quy tắc validate hiện tại

- **Trường dữ liệu bắt buộc:** `id`, `name`, `description` không được phép rỗng hoặc trắng.
- **Ràng buộc giá bán:** Giá trị `basePrice` phải là số thực lớn hơn 0. Nếu bằng 0 hoặc âm sẽ bị từ chối ngay lập tức.
- **Chặn trùng mã ID:** Không cho phép thêm mới nếu `id` sản phẩm trùng lặp với bất kỳ sản phẩm nào hiện có trong thực đơn.
### 8.5 Exception có thể phát sinh khi thêm sản phẩm
- `ValidationException` (Mã lỗi: `INVALID_INPUT`):
  - Giá trị nhập vào bị rỗng, sai định dạng hoặc giá bán gốc của món ăn nhỏ hơn hoặc bằng 0.
- `ValidationException` (Mã lỗi: `DUPLICATE_ID`):
  - Mã định danh của món ăn/thức uống đã có sẵn trong cơ sở dữ liệu thực đơn.
- `IOException`:
  - Sự cố kết nối, phân quyền hoặc lỗi ổ đĩa khi lưu dữ liệu xuống file `menu.csv`. Hệ thống sẽ tự động thực hiện rollback trên RAM ngay sau đó.
### 8.6 Cơ chế Ghi File CSV (mới bổ sung)
- **File đích mặc định:** `data/menu.csv`.
- **Thời điểm đồng bộ:** Ngay lập tức sau khi đối tượng được cập nhật thành công vào repository trên bộ nhớ RAM.
- **Cơ chế ghi:** Thực hiện ghi đè toàn bộ danh sách thực đơn hiện hành dưới dạng Snapshot (không append cuối dòng) để hạn chế tối đa rủi ro lệch luồng dữ liệu.
- **Công cụ chuyển dịch:** Sử dụng lớp trung gian `CsvMapper` để định dạng cấu trúc dòng phẳng đa hình.
Định dạng chi tiết của mỗi dòng dữ liệu CSV:
- **Food:**
  - `FOOD,id,name,basePrice,description,portionSize,isVegetarian` 
- **Drink:**
  - `DRINK,id,name,basePrice,description,size,defaultSugarLevel,defaultIceLevel` 
Ví dụ minh họa:
  - `FOOD,F01,Mì Trộn Tôm Thịt,45000.0,Mì trộn đậm đà,Suất đơn,false` 
  - `DRINK,D01,Trà Sữa Matcha,35000.0,Vị matcha Nhật Bản,M,70,50`
### 8.7 Kiểm tra nhanh dữ liệu file sau khi thêm sản phẩm

Mở Terminal tại thư mục gốc của dự án và chạy các lệnh:

```bash
ls -la data
cat data/menu.csv

```

### 8.8 Kịch bản kiểm thử thủ công (manual test)

#### Ca 1 - Thêm Đồ ăn (Food) hợp lệ:

- **Input:** `id=F01`, `name=Mì Trộn Tôm Thịt`, `basePrice=45000`, `description=Mì trộn siêu ngon`, `portionSize=Suất đơn`, `isVegetarian=false` 
- **Expected:** Giao diện in ra thông báo tạo món ăn thành công, kiểm tra file `menu.csv` xuất hiện dòng dữ liệu mới chuẩn định dạng.
#### Ca 2 - Thêm Đồ uống (Drink) hợp lệ:

- **Input:** `id=D01`, `name=Trà Sữa Matcha`, `basePrice=35000`, `description=Vị matcha ngon`, `size=M`, `defaultSugarLevel=70`, `defaultIceLevel=50`
- **Expected:** Hệ thống lưu thành công, xuất dòng dữ liệu bắt đầu bằng cờ hiệu `DRINK` xuống file CSV.
#### Ca 3 - Mã định danh bị trống:

- **Input:** `id=""`
- **Expected:** Bị hệ thống chặn ngay tại hàm kiểm tra và ném ra lỗi `ValidationException` (Mã lỗi: `INVALID_INPUT`).

#### Ca 4 - Giá bán gốc không hợp lệ:

- **Input:** `basePrice=-35000` hoặc `basePrice=0` 
- **Expected:** Hệ thống kích hoạt kiểm tra biên, chặn đứng tiến trình và ném ra ngoại lệ `ValidationException` (Mã lỗi: `INVALID_INPUT`).
#### Ca 5 - Trùng lặp mã ID món ăn:
- **Input:** Thực hiện thêm một sản phẩm mới (ví dụ: Drink) nhưng cố tình đặt mã trùng `id=F01` (đã tạo ở Ca 1).
- **Expected:** Hệ thống quét bộ nhớ RAM, phát hiện trùng lặp khóa chính, ném ra ngoại lệ `ValidationException` (Mã lỗi: `DUPLICATE_ID`).

#### Ca 6 - Xảy ra lỗi I/O ghi file vật lý:

- **Hành động giả lập:** Khóa quyền ghi hoặc xóa tạm thời thư mục dữ liệu `data` (Permission Denied) rồi tiến hành thêm sản phẩm mới.
- **Expected:** - Hệ thống ném ra lỗi liên quan đến đồng bộ file lưu trữ.
- RAM Repo tự động dọn dẹp bản ghi nháp (Rollback thành công), sản phẩm ẩm thực vừa thêm không còn tồn tại trong bộ nhớ tạm.
