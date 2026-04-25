/**
 * 注册页面 E2E 测试
 */
describe('注册页面测试', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('应该显示注册页面', () => {
    cy.contains('注册').should('exist');
  });

  it('应该有用户名、密码和确认密码输入框', () => {
    cy.get('input').should('have.length.greaterThan', 2);
  });

  it('应该有注册和返回登录按钮', () => {
    cy.contains('注册').should('exist');
    cy.contains('返回登录').should('exist');
  });

  it('点击返回登录应该跳转到登录页面', () => {
    cy.contains('返回登录').click();
    cy.url().should('include', '/login');
  });
});
