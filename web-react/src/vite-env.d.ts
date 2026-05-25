/// <reference types="vite/client" />

interface CoolStoreConfig {
  readonly API_ENDPOINT?: string
  readonly SECURE_API_ENDPOINT?: string
}

interface Window {
  readonly COOLSTORE_CONFIG?: CoolStoreConfig
}
