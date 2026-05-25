import type { Product } from '../api/products'

interface ProductCardProps {
  readonly product: Product
}

export function ProductCard({product}: ProductCardProps) {
  const quantity = product.availability.quantity
  const labelClass = quantity < 15 ? 'pf-m-warning' : 'pf-m-blue'

  return (
    <article className="pf-l-gallery__item" aria-label={product.name} data-testid="product-card">
      <div className="pf-c-card pf-m-compact coolstore-card">
        <div className="pf-c-card__header coolstore-card-image">
          <img src={`/app/imgs/${product.name}.jpg`} alt={product.name} />
        </div>
        <div className="pf-c-card__title">
          <p>{product.name}</p>
          <div className="pf-c-content">
            <small>Provided by Red Hat</small>
          </div>
        </div>
        <div className="pf-c-card__body">{product.description}</div>
        <footer className="pf-c-card__footer coolstore-card-footer">
          <span>{formatCurrency(product.price)}</span>
          {quantity > 0 ? (
            <a className={`pf-c-label pf-m-compact ${labelClass}`} href={product.availability.link ?? '#'} target="_blank" rel="noreferrer">
              <span className="pf-c-label__content">{quantity} left!</span>
            </a>
          ) : (
            <span className="coolstore-stock-empty">Not in Stock</span>
          )}
        </footer>
      </div>
    </article>
  )
}

function formatCurrency(price: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(price)
}
