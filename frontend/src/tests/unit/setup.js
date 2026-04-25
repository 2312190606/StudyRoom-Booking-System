// Jest setup file
global.localStorage = {
  store: {},
  getItem: function(key) {
    return this.store[key] || null;
  },
  setItem: function(key, value) {
    this.store[key] = value.toString();
  },
  removeItem: function(key) {
    delete this.store[key];
  },
  clear: function() {
    this.store = {};
  }
};

// Mock Vant
jest.mock('vant', () => ({
  Popup: {
    name: 'Popup',
    props: ['show', 'position', 'round', 'closeable', 'closeIcon', 'closeIconPosition'],
    template: '<div v-if="show"><slot /></div>'
  },
  Icon: {
    name: 'Icon',
    props: ['name', 'size', 'color'],
    template: '<span>{{ name }}</span>'
  },
  showToast: jest.fn(),
  showSuccessToast: jest.fn(),
  showFailToast: jest.fn(),
  showLoadingToast: jest.fn()
}));

// Mock axios
jest.mock('axios', () => {
  return jest.fn(() => ({
    create: jest.fn(() => ({
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
      interceptors: {
        request: { use: jest.fn() },
        response: { use: jest.fn() }
      }
    }))
  }));
});

// Polyfill import.meta for Vite environment variables
if (typeof global.import === 'undefined') {
  Object.defineProperty(global, 'import', {
    value: { meta: { env: { VITE_API_BASE_URL: '/api' } } },
    writable: true,
    configurable: true
  });
} else {
  global.import.meta = { env: { VITE_API_BASE_URL: '/api' } };
}

// Mock vue-router
jest.mock('vue-router', () => ({
  useRouter: () => ({
    push: jest.fn(),
    back: jest.fn()
  }),
  useRoute: () => ({
    path: '/'
  })
}));