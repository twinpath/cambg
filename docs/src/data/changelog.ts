export const CHANGELOG_PAGE_METADATA = {
  title: "Changelog",
  description:
    "See what's new in CamBG Record. Browse version release notes, enhancements, bug fixes, and download history directly from GitHub releases.",
  badge: "Updates",
  heading: "Changelog",
  subheading:
    "Keep track of all the changes, improvements, and fixes made to CamBG Record.",
} as const

export const CHANGELOG_LIST_CONTENT = {
  labels: {
    unknownDate: "Unknown date",
    noDetails: "No details provided for this release.",
    loadingDetails: "Loading release details...",
    loadMore: "Load More",
  },
} as const

export const RELEASE_TYPE_VARIANT = {
  stable: "default",
  beta: "secondary",
  alpha: "outline",
  test: "destructive",
} as const
