import { expect, test } from '@playwright/test'

test.describe('catalog flow', () => {
  test('renders products loaded through gateway services and database', async ({page}) => {
    await page.goto('/')

    const catalog = page.getByTestId('product-catalog')
    await expect(catalog).toBeVisible()

    const cards = page.getByTestId('product-card')
    await expect(cards).toHaveCount(9)

    await expect(page.getByRole('article', {name: 'Red Fedora'})).toContainText('Official Red Hat Fedora')
    await expect(page.getByRole('article', {name: 'Red Fedora'})).toContainText('$34.99')
    await expect(page.getByRole('article', {name: 'Red Fedora'})).toContainText('Not in Stock')

    await expect(page.getByRole('article', {name: 'Quarkus T-shirt'})).toContainText('$10.00')
    await expect(page.getByRole('article', {name: 'Quarkus T-shirt'})).toContainText('35 left!')

    await expect(page.getByRole('article', {name: 'Pronounced Kubernetes'})).toContainText('12 left!')
  })
})
