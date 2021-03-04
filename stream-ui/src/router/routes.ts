import {RouteConfig} from 'vue-router'
import * as path from "path";

const routes: RouteConfig[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/Index.vue'),
        children: [
          {
            name: 'index',
            path: '',
            redirect: {name: 'home'}
          },
          {
            name: 'home',
            path: 'home',
            component: () => import('pages/index/Home.vue')
          },
          {
            name: 'stars',
            path: 'stars',
            component: () => import('pages/index/Stars.vue')
          },
          {
            name: 'mine',
            path: 'mine',
            component: () => import('pages/index/Mine.vue')
          }
        ]
      },
      {
        name: 'live',
        path: 'live/:id',
        component: () => import('pages/Live.vue')
      }
    ]
  },
  {
    name: "login",
    path: '/login',
    component: () => import('pages/Login.vue')
  },
  // Always leave this as last one,
  // but you can also remove it
  {
    path: '*',
    component: () => import('pages/Error404.vue')
  }
]

export default routes
