/** @type {import('tailwindcss').Config} */
export default {
    content: [
      "./index.html",
      "./src/**/*.{vue,js,ts,jsx,tsx}",
    ],
    theme: {
      extend: {
        fontFamily: {
          'inter': ['Inter', 'sans-serif'],
          'poppins': ['Poppins', 'sans-serif']
        },
        colors: {
          blue: {
            700: '#0F67B1',
            800: '#0D5A9A'
          }
        }
      },
    },
    plugins: [],
  }
  