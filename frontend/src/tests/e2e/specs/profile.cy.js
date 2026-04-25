/**
 * 个人中心页面 E2E 测试
 */
describe('个人中心页面测试', () => {
  beforeEach(() => {
    cy.visit('/login');
    cy.window().then(win => {
      win.localStorage.setItem('token', 'test-token');
      win.localStorage.setItem('userRole', 'user');
      win.localStorage.setItem('userName', '测试用户');
    });
    cy.visit('/profile');
  });

  afterEach(() => {
    cy.clearLocalStorage();
  });

  it('应该显示个人中心页面', () => {
    cy.contains('账户设置').should('exist');
  });

  it('应该显示导航栏', () => {
    cy.contains('静学自习室').should('exist');
    cy.contains('首页').should('exist');
    cy.contains('我的预约').should('exist');
    cy.contains('个人中心').should('exist');
  });

  it('应该显示个人资料卡片', () => {
    cy.contains('个人资料修改').should('exist');
  });

  it('应该显示账户设置选项', () => {
    cy.contains('修改登录密码').should('exist');
  });

  it('应该显示更多支持选项', () => {
    cy.contains('使用指南 & FAQ').should('exist');
    cy.contains('关于静学自习室').should('exist');
  });

  it('应该显示 AI 客服悬浮按钮（全局）', () => {
    cy.get('.fixed.bottom-6').should('exist');
  });
});
