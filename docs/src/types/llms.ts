export interface LlmsFeature {
  name: string
  description: string
}

export interface LlmsRequirement {
  label: string
  detail: string
}

export interface LlmsDocLink {
  title: string
  path: string
  description: string
}

export interface LlmsExternalLink {
  title: string
  url: string
}

export interface LlmsData {
  name: string
  tagline: string
  features: LlmsFeature[]
  requirements: LlmsRequirement[]
  docs: LlmsDocLink[]
  externalLinks: LlmsExternalLink[]
}
