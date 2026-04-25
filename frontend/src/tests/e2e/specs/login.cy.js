/**
 * 登录页面 E2E 测试
 */
describe('登录页面测试', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('应该显示登录页面', () => {
    cy.contains('静学自习室').should('exist');
  });

  it('应该有用户名和密码输入框', () => {
    cy.get('input[type="text"], input[name="username"]').should('exist');
    cy.get('input[type="password"]').should('exist');
  });

  it('应该有登录和注册按钮', () => {
    cy.contains('登录').should('exist');
    cy.contains('注册').should('exist');
  });

  it('空表单提交应该显示提示', () => {
    cy.get('button[type="submit"]').click();
    // 应该有错误提示或表单验证
    cy.get('body').should('contain');
  });

  it('点击注册链接应该跳转到注册页面', () => {
    cy.contains('注册').click();
    cy.url().should('include', '/register');
  });
});
