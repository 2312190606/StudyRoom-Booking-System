module.exports = {
  testEnvironment: 'jsdom',
  transform: {
    '^.+\\.vue$': ['@vue/vue3-jest', { ssr: false }],
    '^.+\\.js$': 'babel-jest'
  },
  moduleFileExtensions: ['vue', 'js', 'json'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy'
  },
  testMatch: [
    '<rootDir>/src/tests/unit/**/*.spec.js',
    '<rootDir>/src/tests/unit/**/*.test.js'
  ],
  collectCoverage: true,
  collectCoverageFrom: [
    'src/api/*.js',
    'src/components/AIChat.vue',
    'src/components/AIChatButton.vue',
    'src/components/Navbar.vue',
    'src/layouts/*.vue',
    'src/views/About.vue',
    'src/views/Guide.vue',
    'src/views/Home.vue',
    'src/views/Login.vue',
    'src/views/Profile.vue',
    'src/views/Register.vue',
    'src/views/Reservations.vue',
    '!src/tests/**',
    '!src/router/**',
    '!src/stores/**',
    '!src/mocks/**',
    '!src/main.js',
    '!**/node_modules/**'
  ],
  coverageDirectory: 'coverage',
  coverageReporters: ['html', 'text', 'lcov']
}