/**
 * 首页 E2E 测试
 */
describe('首页测试', () => {
  beforeEach(() => {
    // 设置 token 以访问需要认证的页面
    cy.visit('/login');
    cy.window().then(win => {
      win.localStorage.setItem('token', 'test-token');
      win.localStorage.setItem('userRole', 'user');
      win.localStorage.setItem('userName', '测试用户');
    });
    cy.visit('/home');
  });

  afterEach(() => {
    cy.clearLocalStorage();
  });

  it('应该显示首页内容', () => {
    cy.contains('静心专注').should('exist');
  });

  it('应该显示导航栏', () => {
    cy.contains('静学自习室').should('exist');
    cy.contains('首页').should('exist');
    cy.contains('我的预约').should('exist');
    cy.contains('个人中心').should('exist');
  });

  it('应该显示自习室概览区域', () => {
    cy.contains('自习室概览').should('exist');
  });

  it('点击我的预约应该跳转到预约页面', () => {
    cy.contains('我的预约').click();
    cy.url().should('include', '/reservations');
  });

  it('应该显示 AI 客服悬浮按钮', () => {
    cy.get('.fixed.bottom-6').should('exist');
    cy.get('.fixed.bottom-6 button').should('exist');
  });
});
