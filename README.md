# Restaurant Client

## Description
Restaurant Client is a comprehensive mobile application designed to streamline order management processes. It aims to provide an efficient and intuitive platform for both administrators and customers to handle orders, products, and user accounts.

## ARROW Server
ARROW (Asynchronous Rust Restaurant Order Workflow) Server is a high-performance backend server designed to streamline restaurant operations. Built with Rust, it leverages the language's safety and concurrency features to deliver a robust and efficient solution for managing orders, inventory, and customer interactions.

## Features
- **User Authentication & Authorization:** Secure login and role-based access control for administrators and customers.
- **Product Management:** Administrators can add, update, and remove products.
- **Order Management:** Customers can browse products, add items to a cart, place orders, and view their order history. Administrators can view and manage all orders.
- **User Management:** Administrators can manage user accounts and roles.
- **Shopping Cart Functionality:** Intuitive cart experience for customers.
- **Checkout Process:** Seamless and secure checkout flow.

## Technologies Used
- **Frontend:** Android (Kotlin with XML layouts via ViewBinding – XML is predominant)
- **Backend:** Rust (ARROW Server)
- **Database:** MySQL
- **Dependency Injection:** Hilt
- **Networking:** Retrofit, OkHttp

## Installation

### Prerequisites
- Android Studio (Version X.Y.Z - *Please specify*)
- JDK (Version X - *Please specify*)
- Git

### Steps
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-repo/RestaurantClient.git
    cd RestaurantClient
    ```
2.  **Open in Android Studio:**
    Open the project in Android Studio.
3.  **Configure Backend API:**
    Create a `.env/.env` file (this folder is git-ignored) and add your backend URL so it stays out of the repository.
    ```properties
    API_BASE_URL=https://your-api.example/
    ```
4.  **Build and Run:**
    Sync Gradle files and run the application on an emulator or a physical device.

## Usage
- **Customer Role:**
    - Register or log in to your account.
    - Browse products and add them to your cart.
    - Proceed to checkout to place an order.
    - View your past orders in the "My Orders" section.
- **Administrator Role:**
    - Log in with administrator credentials.
    - Access the Admin Dashboard to manage products, orders, and users.

## Contributing
(Please specify guidelines for contributions, e.g., pull request process, coding standards.)

## License
BSD 3-Clause License

Copyright (c) 2025, skaarfundgandr

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.