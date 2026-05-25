import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { getProducts, getProductsUrl, resetProductCache } from './products'

describe('products API client', () => {
  beforeEach(() => {
    resetProductCache()
    window.COOLSTORE_CONFIG = {
      API_ENDPOINT: 'http://gateway:8080',
      SECURE_API_ENDPOINT: 'https://secure-gateway.example.test',
    }
  })

  afterEach(() => {
    resetProductCache()
    vi.restoreAllMocks()
  })

  it('builds the products URL from an absolute runtime endpoint', () => {
    expect(getProductsUrl()).toBe('http://gateway:8080/api/products')
  })

  it('loads and caches validated products', async () => {
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(new Response(JSON.stringify([
      {
        name: 'Red Fedora',
        description: 'Classic hat',
        price: 10,
        availability: {quantity: 12, link: 'https://example.test/red-fedora'},
      },
    ])))

    const first = await getProducts(fetcher)
    const second = await getProducts(fetcher)

    expect(first).toEqual(second)
    expect(first[0]?.name).toBe('Red Fedora')
    expect(fetcher).toHaveBeenCalledTimes(1)
  })

  it('rejects failed gateway responses', async () => {
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(new Response('failure', {status: 503}))

    await expect(getProducts(fetcher)).rejects.toThrow('Gateway returned status 503')
  })

  it('rejects malformed product payloads', async () => {
    const fetcher = vi.fn<typeof fetch>().mockResolvedValue(new Response(JSON.stringify([
      {name: 'Broken', availability: {quantity: 1}},
    ])))

    await expect(getProducts(fetcher)).rejects.toThrow('Product description must be a string')
  })
})
