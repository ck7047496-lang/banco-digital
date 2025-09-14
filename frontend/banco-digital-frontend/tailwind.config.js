/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        'primary-purple': '#6B46C1',
        'secondary-blue': '#3182CE',
        'dark-background': '#1A202C',
        'darker-background': '#121826',
        'text-light': '#F7FAFC',
        'text-dark': '#A0AEC0',
        'accent-green': '#38A169',
        'accent-red': '#E53E3E',
      },
    },
  },
  plugins: [],
}
