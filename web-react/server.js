'use strict';

const path = require('path');
const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const probe = require('kube-probe');

const app = express();
const distPath = path.join(__dirname, 'dist');
const indexPath = path.join(distPath, 'index.html');

function getRuntimeConfig() {
  const gatewayService = process.env.COOLSTORE_GW_SERVICE || 'gateway-vertx';
  const namespace = process.env.OPENSHIFT_BUILD_NAMESPACE || 'myproject';

  return {
    API_ENDPOINT: process.env.COOLSTORE_GW_ENDPOINT || `${gatewayService}-${namespace}`,
    SECURE_API_ENDPOINT: process.env.SECURE_COOLSTORE_GW_ENDPOINT || `secure-${gatewayService}-${namespace}`
  };
}

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(cors());

require('./middleware/boom')(app);

app.get('/config.js', (req, res) => {
  res.type('application/javascript');
  res.send(`window.COOLSTORE_CONFIG = ${JSON.stringify(getRuntimeConfig())};`);
});

app.use('/app', express.static(path.join(__dirname, 'app')));
app.use('/', express.static(distPath));

app.get('*splat', (req, res, next) => {
  if (req.path.startsWith('/api/')) {
    next();
    return;
  }

  res.sendFile(indexPath, error => {
    if (error) {
      next(error);
    }
  });
});

probe(app);

app.use((err, req, res, next) => {
  if (res.headersSent) {
    next(err);
    return;
  }

  console.error('Unexpected server error:', err);
  res.status(500).json({error: 'Internal server error'});
});

module.exports = app;
