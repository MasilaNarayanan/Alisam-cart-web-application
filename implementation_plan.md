# MM Printers Web Application

This implementation plan details the creation of "MM Printers", a customized printing e-commerce website inspired by yourprint.in, built on top of the provided Spring Boot + Thymeleaf foundation.

## Proposed Changes

### Configuration
- Update `src/main/resources/application.properties` to configure the MySQL database connection, JPA auto-ddl (`update`), and Thymeleaf cache settings.

### Database Models (JPA Entities)
- **User**: ID, name, email, password, role (ADMIN/USER).
- **Category**: ID, name, description, image_url.
- **Product**: ID, name, description, price, image_url, category_id, is_customizable.
- **Order**: ID, user_id, total_amount, status, created_at.
- **OrderItem**: ID, order_id, product_id, quantity, price, custom_image_url (for customized products).

### Repositories & Services
- Spring Data JPA repositories for the entities (`UserRepository`, `ProductRepository`, `OrderRepository`, `CategoryRepository`).
- Service layer (`ProductService`, `OrderService`, `UserService`) for business logic.

### Web Controllers
- **HomeController**: Maps `/` to `index.html`.
- **ProductController**: Maps `/products` and `/products/{id}`.
- **CartController**: Manages an in-memory or session-based shopping cart.
- **CheckoutController**: Handles order placement.
- **AuthController**: Manages user login and registration.

### Frontend Views (Thymeleaf - Premium Design)
All templates will be built with a highly responsive, modern, premium design aesthetic involving curated color palettes, glassmorphism UI elements, and micro-animations.

- `src/main/resources/templates/fragments/layout.html`: Base layout containing the Header (Navigation, Cart icon) and Footer.
- `src/main/resources/templates/index.html`: Home page featuring a dynamic Hero section, "Featured Products", and "Shop by Category" grids.
- `src/main/resources/templates/products.html`: E-commerce catalog listing.
- `src/main/resources/templates/product-details.html`: Product view with an interface for users to "upload a design" or select customization options.
- `src/main/resources/templates/cart.html` & `checkout.html`: Shopping cart and order summary pages.

### Static Assets
- `src/main/resources/static/css/style.css`: Core stylesheet containing the premium design tokens, vibrant colors, shadows, and smooth hover micro-animations.
- `src/main/resources/static/js/main.js`: Setup interactivity (e.g., image upload preview for custom prints, cart dynamic updates).

## Verification Plan

### Automated Tests
- Run `mvn test` to ensure Spring context loads properly without dependency issues.

### Manual Verification
1. Run the Application using Spring Boot Maven Plugin (`mvn spring-boot:run` from `Alisam_Codes` directory).
2. Open the browser to `http://localhost:8080`.
3. Verify that the "MM Printers" home page loads with premium styling.
4. Navigate through categories and products.
5. Attempt the "Upload Design" flow on a customizable product (e.g., Custom Mug).
6. Add item to the Cart and proceed to checkout.
7. Verify order creation in the local MySQL database.
