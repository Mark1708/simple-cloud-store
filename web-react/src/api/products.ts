export interface ProductAvailability {
  readonly quantity: number
  readonly link?: string
}

export interface Product {
  readonly name: string
  readonly description: string
  readonly price: number
  readonly availability: ProductAvailability
}

interface ProductRecord {
  readonly name?: unknown
  readonly description?: unknown
  readonly price?: unknown
  readonly availability?: unknown
}

interface AvailabilityRecord {
  readonly quantity?: unknown
  readonly link?: unknown
}

let cachedProducts: readonly Product[] | null = null

export function resetProductCache(): void {
  cachedProducts = null
}

export function getProductsUrl(location: Location = window.location): string {
  const config = window.COOLSTORE_CONFIG ?? {}
  const isSecure = location.protocol === 'https:'
  const endpoint = isSecure ? config.SECURE_API_ENDPOINT : config.API_ENDPOINT
  const fallbackEndpoint = isSecure ? 'secure-gateway-vertx-myproject' : 'gateway-vertx-myproject'
  const selectedEndpoint = endpoint ?? fallbackEndpoint
  const protocol = isSecure ? 'https://' : 'http://'

  if (selectedEndpoint.startsWith('http://') || selectedEndpoint.startsWith('https://')) {
    return `${selectedEndpoint}/api/products`
  }

  return `${protocol}${selectedEndpoint}.${getHostSuffix(location.host)}/api/products`
}

export async function getProducts(fetcher: typeof fetch = fetch): Promise<readonly Product[]> {
  if (cachedProducts !== null) {
    return cachedProducts
  }

  const response = await fetcher(getProductsUrl())

  if (!response.ok) {
    throw new Error(`Gateway returned status ${response.status}`)
  }

  const payload: unknown = await response.json()
  const products = parseProducts(payload)
  cachedProducts = products
  return products
}

function getHostSuffix(host: string): string {
  const firstDotIndex = host.indexOf('.')
  return firstDotIndex === -1 ? host : host.slice(firstDotIndex + 1)
}

function parseProducts(payload: unknown): readonly Product[] {
  if (!Array.isArray(payload)) {
    throw new Error('Gateway response must be an array')
  }

  return payload.map(parseProduct)
}

function parseProduct(value: unknown): Product {
  if (!isRecord(value)) {
    throw new Error('Product must be an object')
  }

  const product = value as ProductRecord
  const availability = parseAvailability(product.availability)

  if (typeof product.name !== 'string') {
    throw new Error('Product name must be a string')
  }

  if (typeof product.description !== 'string') {
    throw new Error('Product description must be a string')
  }

  if (typeof product.price !== 'number') {
    throw new Error('Product price must be a number')
  }

  return {
    name: product.name,
    description: product.description,
    price: product.price,
    availability,
  }
}

function parseAvailability(value: unknown): ProductAvailability {
  if (!isRecord(value)) {
    throw new Error('Product availability must be an object')
  }

  const availability = value as AvailabilityRecord

  if (typeof availability.quantity !== 'number') {
    throw new Error('Product availability quantity must be a number')
  }

  return {
    quantity: availability.quantity,
    link: typeof availability.link === 'string' ? availability.link : undefined,
  }
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null
}
