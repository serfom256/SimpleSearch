import { Container, Title, Tabs } from '@mantine/core'
import { ClusterCheckForm } from './components/ClusterCheck'
import { CreateIndexForm } from './components/CreateIndexForm'
import { SearchForm } from './components/SearchForm'

export default function App() {
  return (
    <Container size="md" py="md">
      <Title mb="md">Simple Search</Title>

      <Tabs defaultValue="search">
        <Tabs.List>
          <Tabs.Tab value="search">Cluster Check</Tabs.Tab>
          <Tabs.Tab value="indexing">Indexing</Tabs.Tab>
          <Tabs.Tab value="cluster">Cluster Check</Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="search" pt="md">
          <SearchForm />
        </Tabs.Panel>

        <Tabs.Panel value="indexing" pt="md">
          <CreateIndexForm />
        </Tabs.Panel>

        <Tabs.Panel value="cluster" pt="md">
          <ClusterCheckForm />
        </Tabs.Panel>
      </Tabs>
    </Container>
  )
}