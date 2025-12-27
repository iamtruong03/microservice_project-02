# React Frontend - Microservice Platform

## 🚀 Quick Start

### Prerequisites
- Node.js 16+ and npm
- Backend services running (or accessible at localhost:8080)

### Installation

```bash
# Install dependencies
npm install
```

### Development

```bash
# Start development server
npm run dev
```

The frontend will be available at **http://localhost:5173**

### Build for Production

```bash
# Build the project
npm run build

# Preview production build
npm run preview
```

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/          # Reusable React components
│   │   └── MainLayout.jsx   # Main layout with navigation
│   ├── pages/               # Page components
│   │   ├── Dashboard.jsx    # Dashboard page
│   │   ├── Orders.jsx       # Orders management
│   │   ├── Inventory.jsx    # Inventory management
│   │   ├── Accounting.jsx   # Accounting transactions
│   │   └── Notifications.jsx # Notification system
│   ├── services/            # API services
│   │   ├── api.js           # Axios instance with interceptors
│   │   └── index.js         # Service methods for each microservice
│   ├── App.jsx              # Main app component
│   ├── main.jsx             # Entry point
│   └── App.css              # Global styles
├── public/                  # Static assets
├── package.json             # Dependencies
├── vite.config.js          # Vite configuration
├── .env.development        # Development environment variables
├── .env.production         # Production environment variables
└── Dockerfile              # Docker configuration
```

## 🛠️ Technology Stack

- **React 18.2** - UI framework
- **Vite** - Build tool and dev server
- **React Router DOM 6** - Client-side routing
- **Axios** - HTTP client
- **Ant Design 5** - UI component library
- **Lodash ES** - Utility functions
- **Day.js** - Date manipulation

## 📡 API Integration

The frontend communicates with the backend through the **API Gateway** at `http://localhost:8080/api`.

### Supported Endpoints:

**Orders:**
- `GET /orders` - Get all orders
- `POST /orders` - Create new order
- `PUT /orders/:id` - Update order
- `DELETE /orders/:id` - Delete order

**Inventory:**
- `GET /inventory` - Get all inventory
- `POST /inventory` - Create inventory
- `POST /inventory/reserve` - Reserve items
- `PUT /inventory/:id` - Update inventory

**Accounting:**
- `GET /accounting/transactions` - Get all transactions
- `POST /accounting/transactions` - Create transaction
- `PUT /accounting/transactions/:id` - Update transaction

**Notifications:**
- `POST /notifications` - Send notification
- `GET /notifications/health` - Check service health

## 🎨 Features

### Dashboard
- Real-time statistics
- Service status overview
- Quick links to services

### Orders Management
- Create, read, update, delete orders
- View order details and status
- Filter and search orders

### Inventory Management
- Manage inventory items
- View stock levels
- Reserve inventory
- Real-time quantity updates

### Accounting
- Create and manage transactions
- View transaction history
- Track debits and credits
- Filter by order or customer

### Notifications
- Send custom notifications
- View notification history
- Service health monitoring
- Automatic event-driven notifications

## 🔧 Environment Variables

### Development (`.env.development`)
```
VITE_API_URL=http://localhost:8080/api
VITE_APP_NAME=Microservice Platform
```

### Production (`.env.production`)
```
VITE_API_URL=/api
VITE_APP_NAME=Microservice Platform
```

## 📦 Docker

### Build Docker Image

```bash
docker build -t microservice-frontend:latest .
```

### Run Docker Container

```bash
docker run -p 5173:5173 \
  -e VITE_API_URL=http://localhost:8080/api \
  microservice-frontend:latest
```

### Docker Compose

The frontend is included in the main docker-compose.yml. To add it:

```yaml
frontend:
  build:
    context: ./frontend
  ports:
    - "5173:5173"
  environment:
    - VITE_API_URL=http://api-gateway:8080/api
  depends_on:
    - api-gateway
```

## 🚦 Available Scripts

```bash
# Development
npm run dev           # Start dev server

# Build
npm run build         # Build for production
npm run preview       # Preview production build

# Linting
npm run lint          # Run ESLint
npm run lint:fix      # Fix ESLint issues
```

## 🌐 Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## 📱 Responsive Design

The application is fully responsive and works on:
- Desktop (1920px and above)
- Tablet (768px - 1919px)
- Mobile (below 768px)

## 🔐 Security

- CORS enabled for API calls
- Axios request/response interceptors
- Token-based authentication ready
- XSS protection via React

## 🚨 Troubleshooting

### CORS Error
- Ensure backend CORS is properly configured
- Check API Gateway proxy settings

### API Calls Failing
- Verify backend services are running
- Check API Gateway at `http://localhost:8080`
- Review browser console for errors

### Port Already in Use
```bash
# Find process using port 5173
npx kill-port 5173

# Or change port in vite.config.js
```

### Dependencies Issues
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

## 📊 Performance

- Code splitting with React lazy
- Optimized bundle size (~150KB gzipped)
- Lazy loading of pages
- CSS-in-JS optimization
- Image optimization ready

## 🎯 Best Practices

- Component-based architecture
- Service layer for API calls
- Environment-based configuration
- Error handling and loading states
- Responsive design patterns
- Accessibility considerations

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Run `npm run lint:fix`
4. Commit and push
5. Create a pull request

## 📄 License

MIT License

## 📞 Support

For issues or questions, refer to the backend documentation or check the main project README.

---

**Happy Coding! 🎉**
