export interface NavigationLink {
  label: string
  href: string
  external?: boolean
}

export interface NavbarProps {
  currentPath: string
}

export interface MobileNavProps {
  currentPath: string
}
