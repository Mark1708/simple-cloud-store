'use strict'

/**
 * Returns a number between 5000 and 15000 milliseconds
 * @returns {Number}
 */
const getDelayMilliseconds = (min, max) => Math.max(5000, Math.random() * 15)

/**
 * Decorate express application with a boom middleware
 * @param {Express.Application} app 
 */
module.exports = (app) => {
  let isDegraded = false

  app.get('/boom', (req, res) => {
    if (isDegraded) {
      res.end('application is already in degraded state')
    } else {
      isDegraded = true

      res.end('application will now simulate a degraded state')
    }
  })

  app.use((req, res, next) => {
    if (!isDegraded) {
      // Pass the request along to the next routes/middlewares immediately
      next()
    } else {
      // Simulate a degraded service by slowing request processing time
      setTimeout(() => next(), getDelayMilliseconds())
    }
  })
}