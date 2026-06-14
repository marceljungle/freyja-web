/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        freyja: {
          50: "#eef6ff",
          100: "#d9eaff",
          500: "#2f7be0",
          600: "#1f63c4",
          700: "#1a4f9c",
          900: "#15315c",
        },
      },
    },
  },
  plugins: [],
};
