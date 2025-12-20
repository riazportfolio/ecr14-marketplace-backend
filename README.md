# ECR14 Marketplace Backend

Spring Boot backend for ECR14 Marketplace - an apartment community entrepreneurs directory platform.

## Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Database**: MySQL 8.0+
- **Authentication**: JWT (JSON Web Tokens)
- **Image Hosting**: Cloudinary
- **Build Tool**: Maven

## Features

- JWT-based authentication and authorization
- Role-based access control (SuperAdmin, Admin, Customer, Guest)
- RESTful API endpoints for brands, products, and categories
- Image upload integration with Cloudinary
- Comprehensive exception handling
- BCrypt password hashing

## Prerequisites

- JDK 17 or higher
- Maven 3.6+
- MySQL 8.0+ running on port 3307
- Cloudinary account (for image hosting)

## Database Setup

1. Ensure MySQL is running on port 3307
2. Create database:
   ```sql
   CREATE DATABASE marketplace;
   ```

## Environment Variables

Set the following environment variables before running the application:

```bash
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
JWT_SECRET=your_secure_random_secret_key_minimum_256_bits
```

### Windows (PowerShell)
```powershell
$env:CLOUDINARY_CLOUD_NAME="your_cloud_name"
$env:CLOUDINARY_API_KEY="your_api_key"
$env:CLOUDINARY_API_SECRET="your_api_secret"
$env:JWT_SECRET="your_secure_random_secret_key"
```

### Linux/Mac
```bash
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_api_key
export CLOUDINARY_API_SECRET=your_api_secret
export JWT_SECRET=your_secure_random_secret_key
```

## Build and Run

### Using Maven

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Using JAR

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/marketplace-backend-1.0.0.jar
```

The application will start on **http://localhost:8080**

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login with phone and password
- `POST /api/auth/register` - Register new customer
- `POST /api/auth/logout` - Logout (client-side token removal)
- `GET /api/auth/check-password?phone={phone}` - Check if password required

### Products (Public GET, Protected POST/PUT/DELETE)
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/brand/{brandId}` - Get products by brand
- `POST /api/products` - Create product (Admin/SuperAdmin)
- `PUT /api/products/{id}` - Update product (Admin/SuperAdmin)
- `DELETE /api/products/{id}` - Delete product (Admin/SuperAdmin)

### Brands (Public GET, Protected POST/PUT/DELETE)
- `GET /api/brands` - Get all brands
- `GET /api/brands/{id}` - Get brand by ID
- `GET /api/brands/owner/{ownerId}` - Get brand by owner
- `POST /api/brands` - Create brand (Admin/SuperAdmin)
- `PUT /api/brands/{id}` - Update brand (Admin/SuperAdmin)
- `DELETE /api/brands/{id}` - Delete brand (Admin/SuperAdmin)

### Categories (Public)
- `GET /api/categories` - Get all categories

### Images (Protected)
- `POST /api/images/upload` - Upload image to Cloudinary (Admin/SuperAdmin)
- `DELETE /api/images/{publicId}` - Delete image from Cloudinary (Admin/SuperAdmin)

## Demo Credentials

### Super Admin
- Phone: `9876543210`
- Password: `super123`

### Admin Users
- **Amma's Kitchen**
  - Phone: `9876543211`
  - Password: `admin123`

- **Sweet Delights**
  - Phone: `9876543212`
  - Password: `admin123`

### Customer
- Phone: `9876543213`
- Password: Not required

## Database Schema

The application uses the following main tables:
- `users` - User accounts with roles
- `brands` - Brand information
- `brand_categories` - Brand category associations
- `products` - Product listings
- `categories` - Product categories

## Project Structure

```
src/
├── main/
│   ├── java/com/ecr14/marketplace/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Custom exceptions
│   │   ├── repository/      # JPA repositories
│   │   ├── security/        # Security components
│   │   ├── service/         # Business logic
│   │   └── util/            # Utility classes
│   └── resources/
│       ├── application.yml  # Application configuration
│       └── data.sql         # Initial seed data
└── test/                    # Test files
```

## CORS Configuration

The backend is configured to accept requests from:
- http://localhost:5173 (Vite dev server)
- http://localhost:3000 (Alternative React dev server)

Update `CorsConfig.java` for production domains.

## Security Features

- JWT token-based authentication
- BCrypt password hashing (strength 12)
- Role-based authorization
- Method-level security with @PreAuthorize
- Stateless session management

## Integration with React UI

Update the React UI API stubs in `ecr14-marketplace-ui/src/api/stubs.ts` to point to:
```
http://localhost:8080/api
```

Replace the mock functions with actual HTTP calls using axios or fetch.

## Troubleshooting

### Database Connection Issues
- Ensure MySQL is running on port 3307
- Verify database credentials in `application.yml`
- Check if `marketplace` database exists

### Cloudinary Upload Fails
- Verify environment variables are set correctly
- Check Cloudinary credentials are valid
- Ensure file size is under 10MB

### JWT Token Errors
- Ensure JWT_SECRET is at least 256 bits (32 characters)
- Check token expiration (7 days by default)

## License

Private project for ECR14 community.
