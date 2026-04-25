module.exports = {
  testEnvironment: 'jsdom',
  transform: {
    '^.+\\.vue$': ['@vue/vue3-jest', { ssr: false }],
    '^.+\\.js$': 'babel-jest'
  },
  moduleFileExtensions: ['vue', 'js', 'json'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
    '^vant$': '<rootDir>/node_modules/vant/lib/vant.cjs.js'
  },
  testMatch: [
    '<rootDir>/src/tests/unit/**/*.spec.js',
    '<rootDir>/src/tests/unit/**/*.test.js'
  ],
  collectCoverage: true,
  collectCoverageFrom: [
    'src/api/*.js',
    '!src/tests/**',
    '!src/router/**',
    '!src/stores/**',
    '!src/mocks/**',
    '!src/main.js',
    '!**/node_modules/**'
  ],
  coverageDirectory: 'coverage',
  coverageReporters: ['html', 'text', 'lcov'],
  setupFilesAfterEnv: ['<rootDir>/src/tests/unit/setup.js'],
  globals: {
    'import.meta.env': {
      VITE_API_BASE_URL: '/api'
    }
  }
}