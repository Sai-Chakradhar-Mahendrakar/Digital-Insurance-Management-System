# Use Node.js v22.12.0 as base image
FROM node:22.12.0-alpine

# Set working directory to /frontend
WORKDIR /frontend

# Echo Node.js and npm versions for verification
RUN echo "Node.js version:" && node -v && echo "npm version:" && npm -v

# Install global dependencies
RUN npm install -g @vue/cli vite

# Verify Vue CLI and Vite versions
RUN echo "Vue CLI version:" && vue --version && echo "Vite version:" && vite --version

# Copy package.json and package-lock.json (if available) from build context
COPY package*.json ./

# Install project dependencies
RUN npm install

# Copy all frontend project files from build context
COPY . .

# Build for production
RUN npm run build

# Expose port
EXPOSE 5173

# Start the application
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]