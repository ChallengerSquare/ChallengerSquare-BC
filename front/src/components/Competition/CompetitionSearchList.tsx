import React, { useState, useEffect, Dispatch, SetStateAction } from 'react'
import { useRecoilState } from 'recoil'
import { Link } from 'react-router-dom'
import { ContestData } from '@/types/competition'
import useScrollTop from '@/hooks/useScrollTop'
import styles from '@/components/Competition/CompetitionSearchList.module.scss'
import ProfileImg from '../ProfileImg/ProfileImg'
import EmptyImg from '../EmptyImg/EmptyImg'

interface SearchListProps {
  title: string
  data: ContestData[]
  orderBy: number
  setOrderBy: Dispatch<SetStateAction<number>>
}

const CompetitionSearchList = ({ title, data, orderBy, setOrderBy }: SearchListProps) => {
  useScrollTop()
  const [items, setItems] = useState<ContestData[]>([...data])

  useEffect(() => {
    setItems([...data])
  }, [data])

  const handleSortChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setOrderBy(parseInt(event.target.value, 10))
  }

  return (
    <div>
      <div className={styles.title}>
        <p>{title}</p>
        <select onChange={handleSortChange} value={orderBy}>
          <option value="3">최신 등록순</option>
          <option value="4">이름순</option>
        </select>
      </div>
      <div className={styles.content}>
        {items.length > 0 ? (
          <div className={styles.grid}>
            {items.map((item: ContestData, index) => (
              <div key={index} className={styles.grid_item}>
                <Link to={`/competition/detail/${item.contestId}`}>
                  <ProfileImg imgUrl={item.contestImage} imgName={item.contestTitle} name={'competition'} />
                  <p>{item.contestTitle}</p>
                  <span>{item.contestDate}</span>
                </Link>
              </div>
            ))}
          </div>
        ) : (
          <div className={styles.empty}>
            <EmptyImg text={'검색된 결과가 없습니다.'} />
          </div>
        )}
      </div>
    </div>
  )
}

export default CompetitionSearchList
