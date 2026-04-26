/**
 * AI 客服功能 E2E 测试
 */
describe('AI 客服功能测试', () => {
  beforeEach(() => {
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

  it('应该显示 AI 客服悬浮按钮', () => {
    cy.get('.fixed.bottom-6').should('exist');
  });

  it('点击悬浮按钮应该打开对话窗口', () => {
    cy.get('.fixed.bottom-6 button').click();
    cy.contains('自习室小助手').should('exist');
  });

  it('对话窗口应该包含欢迎消息', () => {
    cy.get('.fixed.bottom-6 button').click();
    cy.contains('你好！我是静学自习室的小助手').should('exist');
  });

  it('应该显示输入框', () => {
    cy.get('.fixed.bottom-6 button').click();
    cy.get('input[placeholder="输入问题..."]').should('exist');
  });

  it('应该有关闭按钮', () => {
    cy.get('.fixed.bottom-6 button').click();
    cy.get('.popup-mock').should('exist');
  });

  it('关闭按钮应该可以关闭对话窗口', () => {
    cy.get('.fixed.bottom-6 button').click();
    cy.get('.popup-mock .close, .popup-mock button').first().click();
    // 验证弹窗已关闭
    cy.get('.popup-mock').should('not.exist');
  });
});
