import { useEffect, useState } from 'react'

import { getProducts, type Product } from '../api/products'
import { ProductCard } from './ProductCard'

type ProductsState =
  | { readonly status: 'loading'; readonly products: readonly Product[] }
  | { readonly status: 'ready'; readonly products: readonly Product[] }
  | { readonly status: 'error'; readonly products: readonly Product[]; readonly message: string }

export function App() {
  const [state, setState] = useState<ProductsState>({status: 'loading', products: []})

  useEffect(() => {
    let isCurrent = true

    getProducts()
      .then(products => {
        if (isCurrent) {
          setState({status: 'ready', products})
        }
      })
      .catch(error => {
        if (isCurrent) {
          setState({
            status: 'error',
            products: [],
            message: error instanceof Error ? error.message : 'Failed to load products',
          })
        }
      })

    return () => {
      isCurrent = false
    }
  }, [])

  return (
    <div className="pf-c-page coolstore-page">
      <Header />
      <main className="pf-c-page__main" tabIndex={-1}>
        <section className="pf-c-page__main-section" aria-label="Product catalog">
          {state.status === 'error' ? (
            <div className="pf-c-alert pf-m-danger" aria-live="polite">
              <div className="pf-c-alert__title">Failed to load product data</div>
              <p className="pf-c-alert__description">{state.message}</p>
            </div>
          ) : null}
          <div className="pf-l-gallery pf-m-gutter" data-testid="product-catalog">
            {state.products.map(product => (
              <ProductCard key={product.name} product={product} />
            ))}
          </div>
        </section>
      </main>
    </div>
  )
}

function Header() {
  return (
    <header className="pf-c-page__header coolstore-header">
      <div className="pf-c-page__header-brand">
        <a className="pf-c-page__header-brand-link coolstore-brand" href="/">
          <img src="/app/imgs/logo.png" alt="CoolStore" className="coolstore-logo" />
          <span>Red Hat CoolStore Microservices App</span>
        </a>
      </div>
    </header>
  )
}
