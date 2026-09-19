import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/enclosures' },
  { path: '/enclosures', component: () => import('../views/Enclosures.vue'), meta: { label: '笼舍' } },
  { path: '/animals', component: () => import('../views/Animals.vue'), meta: { label: '动物' } },
  { path: '/feedings', component: () => import('../views/Feedings.vue'), meta: { label: '投喂' } },
  { path: '/isolation-meals', component: () => import('../views/IsolationMeals.vue'), meta: { label: '隔离加餐' } },
  { path: '/checks', component: () => import('../views/Checks.vue'), meta: { label: '兽医巡查' } }
]

const mods = routes.filter((r) => r.meta).map((r) => ({ path: r.path, label: r.meta.label }))

export default createRouter({
  history: createWebHistory(),
  routes: [
    ...routes.slice(0, 1),
    ...routes.slice(1).map((r) => ({ path: r.path, component: r.component, meta: r.meta }))
  ]
})

export { mods }
