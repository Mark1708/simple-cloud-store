import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'

import { ProductCard } from './ProductCard'

describe('ProductCard', () => {
  it('renders available product stock and price', () => {
    render(
      <ProductCard
        product={{
          name: 'Red Fedora',
          description: 'Classic hat',
          price: 10,
          availability: {quantity: 12, link: 'https://example.test/red-fedora'},
        }}
      />,
    )

    expect(screen.getByText('Red Fedora')).toBeInTheDocument()
    expect(screen.getByText('Classic hat')).toBeInTheDocument()
    expect(screen.getByText('$10.00')).toBeInTheDocument()
    expect(screen.getByText('12 left!')).toBeInTheDocument()
  })

  it('renders out of stock products', () => {
    render(
      <ProductCard
        product={{
          name: 'Blue Fedora',
          description: 'Unavailable hat',
          price: 11,
          availability: {quantity: 0},
        }}
      />,
    )

    expect(screen.getByText('Not in Stock')).toBeInTheDocument()
  })
})
