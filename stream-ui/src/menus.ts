export class Menu {
  name: string | null | undefined
  icon: string | null | undefined
  to: string | null | undefined

  public constructor(name: string | null | undefined,
                     icon: string | null | undefined,
                     to: string | null | undefined) {
    this.name = name
    this.icon = icon
    this.to = to
  }
}

const menus = {
  main: [
    new Menu('home', 'home', 'home'),
    new Menu('stars', 'star', 'stars'),
    new Menu('mine', 'person', 'mine'),
    // new Menu('home', 'home', 'index')
  ]
}

export default menus
