module.exports = {
  presets: [
    ['@babel/preset-env', { targets: { node: 'current' } }]
  ],
  plugins: [
    '@babel/plugin-syntax-import-meta',
    ['babel-plugin-transform-vite-meta-env', { VITE_API_BASE_URL: '/api' }]
  ]
}
