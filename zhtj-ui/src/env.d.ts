/// <reference types="vite/client" />

interface ImportMeta {
  readonly env: {
    readonly MODE: string
    readonly BASE_URL: string
    readonly DEV: boolean
    readonly PROD: boolean
    readonly SSR: boolean
  }
  readonly glob: (glob: string, options?: {
    eager?: boolean
    as?: string
  }) => Record<string, any>
}

// 支持静态资源导入
declare module '*.png'
declare module '*.jpg'
declare module '*.jpeg'
declare module '*.gif'
declare module '*.svg'
declare module '*.ico' 