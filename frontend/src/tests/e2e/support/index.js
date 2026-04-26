// Cypress 支持文件 - 入口
import './commands';

// 自定义命令
Cypress.Commands.add('login', (username, password) => {
  cy.visit('/login');
  cy.get('input[type="text"], input[name="username"]').first().type(username);
  cy.get('input[type="password"]').type(password);
  cy.get('button[type="submit"], button:contains("登录")').click();
});

Cypress.Commands.add('logout', () => {
  cy.clearLocalStorage();
  cy.visit('/login');
});
