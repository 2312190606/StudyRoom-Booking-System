/**
 * 预约页面 E2E 测试
 */
describe('预约页面测试', () => {
  beforeEach(() => {
    cy.visit('/login');
    cy.window().then(win => {
      win.localStorage.setItem('token', 'test-token');
      win.localStorage.setItem('userRole', 'user');
      win.localStorage.setItem('userName', '测试用户');
    });
    cy.visit('/reservations');
  });

  afterEach(() => {
    cy.clearLocalStorage();
  });

  it('应该显示预约页面', () => {
    cy.contains('我的预约').should('exist');
  });

  it('应该显示导航栏', () => {
    cy.contains('静学自习室').should('exist');
  });

  it('应该显示当前/历史预约标签页', () => {
    cy.contains('当前预约').should('exist');
    cy.contains('历史记录').should('exist');
  });
});
