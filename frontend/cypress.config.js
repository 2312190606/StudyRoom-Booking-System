import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:5173',
    supportFile: 'src/tests/e2e/support/index.js',
    specPattern: 'src/tests/e2e/specs/**/*.cy.js',
    video: false,
    screenshotOnRunFailure: true,
    setupNodeEvents(on, config) {
      // node event handling
    },
  },
  component: {
    devServer: {
      framework: 'vue',
      bundler: 'vite',
    },
  },
});
