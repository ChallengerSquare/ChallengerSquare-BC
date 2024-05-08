import Navbar from '@/components/Navbar/Navbar'
import CompetitionList from '@/components/Competition/CompetitionList'
import Search from '@/components/Search/Search'
import styles from './Competition.module.scss'

const items = [
  { id: 1, image: '이미지1', name: '이미지1', date: '2024-04-29' },
  { id: 2, image: '이미지2', name: '이미지2', date: '2024-04-29' },
  { id: 3, image: '이미지3', name: '이미지3', date: '2024-04-29' },
  { id: 4, image: '이미지4', name: '이미지4', date: '2024-04-29' },
  { id: 5, image: '이미지5', name: '이미지5', date: '2024-04-29' },
]

const Competition = () => {
  return (
    <div>
      <div className={styles.background}>
        <Navbar enableScrollEffect />
        <div className={styles.searchbar}>
          <Search />
        </div>
      </div>
      <div className={styles.searchlist}>
        <CompetitionList text="인기 있는 대회를 살펴보세요" items={items} />
        <CompetitionList text="마감 임박한 대회를 살펴보세요" items={items} />
      </div>
    </div>
  )
}

export default Competition