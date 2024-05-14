import { useState, useEffect, useInsertionEffect } from 'react'
import { User } from '@/types/user'
import styles from '@/pages/mypage/myinfo/MyInfo.module.scss'
import Button from '@/components/Button/Button'
import loadPostcode from '@/services/postcode'

const MyInfo = () => {
  const [user, setUser] = useState<User>({
    userName: '',
    userBirth: '',
    userContact: '',
    userAddress: '',
    userEmail: '',
  })
  const [editUser, setEditUser] = useState<User>({
    userName: '',
    userBirth: '',
    userContact: '',
    userAddress: '',
    userEmail: '',
  })
  const [addressDetails, setAddressDetails] = useState({
    postcode: '',
    roadAddress: '',
    detailAddress: '',
  })
  const [address, setAddress] = useState('')

  useEffect(() => {
    /* User 가져오는 API 호출 */
    setUser({
      userName: '김땡땡',
      userBirth: '2000-01-01',
      userContact: '010-1234-5678',
      userAddress: '06220 서울 강남구 테헤란로 212 (역삼동, 멀티캠퍼스) 멀티캠퍼스 8층',
      userEmail: 'multicampus@gmail.com',
    })
  }, [])

  useEffect(() => {
    settingData()
  }, [user])

  const settingData = () => {
    setEditUser(user)
    if (user.userAddress != '') {
      setAddressDetails({
        postcode: user.userAddress.split(' ')[0],
        roadAddress: `${user.userAddress.split(' ').slice(1).join(' ').split(')')[0]})`,
        detailAddress: '',
      })
      setAddress(`${user.userAddress.split(')')[1].split(' ').slice(1).join(' ')}`)
    }
  }

  const handleUser = (key: string, event: React.ChangeEvent<HTMLInputElement>) => {
    if (key === 'address') {
      const fullAddress = `${addressDetails.postcode} ${addressDetails.roadAddress} ${event.target.value}`
      setAddress(event.target.value)
      setEditUser((prev) => ({ ...prev, [key]: fullAddress }))
    } else setEditUser((prev) => ({ ...prev, [key]: event.target.value }))
  }

  const handelePostRequest = () => {
    /* 수정된 user post API 호출 */
    setUser(editUser)
  }

  return (
    <div className={styles.info}>
      <div className={styles.title}>
        <div>{'HOME > 내정보'}</div>
        <button type={'button'} className={styles.btn}>
          {'회원탈퇴'}
        </button>
      </div>
      <div className={styles.content}>
        <div className={styles.input_wrap}>
          <div className={styles.input_title}>
            <label htmlFor={'username'}>{'이름'}</label>
          </div>
          <div className={styles.input}>
            <input
              id={'username'}
              type={'text'}
              value={editUser.userName}
              onChange={(event) => handleUser('username', event)}
            />
          </div>
        </div>
        <div className={styles.input_wrap}>
          <div className={styles.input_title}>
            <label htmlFor={'email'}>{'이메일'}</label>
          </div>
          <div className={styles.input}>
            <div className={styles.input}>
              <input
                id={'email'}
                type={'text'}
                value={editUser.userEmail}
                onBlur={(event) => handleUser('email', event)}
                disabled
              />
            </div>
          </div>
        </div>
        <div className={styles.input_wrap}>
          <div className={styles.input_title}>
            <label htmlFor={'birth'}>{'생년월일'}</label>
          </div>
          <div className={styles.input}>
            <div className={styles.input}>
              <input
                id={'birth'}
                type={'text'}
                value={editUser.userBirth}
                onChange={(event) => handleUser('birth', event)}
                disabled
              />
            </div>
          </div>
        </div>
        <div className={styles.input_wrap}>
          <div className={styles.input_title}>
            <label htmlFor={'contact'}>{'전화번호'}</label>
          </div>
          <div className={styles.input}>
            <input
              id={'contact'}
              type={'text'}
              value={editUser.userContact}
              onChange={(event) => handleUser('contact', event)}
            />
          </div>
        </div>
        <div className={styles.input_wrap}>
          <div className={styles.input_title}>
            <label htmlFor={'address'}>{'주소'}</label>
          </div>
          <div className={styles.input}>
            <input type={'text'} value={addressDetails.postcode} readOnly />
            <button type={'button'} onClick={() => loadPostcode(setAddressDetails)}>
              {'주소검색'}
            </button>
          </div>
        </div>
        <div className={styles.input_wrap}>
          <div className={styles.input_title}>
            <div>{''}</div>
          </div>
          <div className={styles.input}>
            <input className={styles.address} type={'text'} value={addressDetails.roadAddress} readOnly />
          </div>
        </div>
        <div className={styles.input_wrap}>
          <div className={styles.input_title}>
            <div>{''}</div>
          </div>
          <div className={styles.input}>
            <input
              className={styles.address}
              id={'address'}
              type={'text'}
              value={address}
              onChange={(event) => handleUser('address', event)}
            />
          </div>
        </div>
        <div className={styles.button_wrap}>
          <Button variation={'purple default'} onClick={handelePostRequest}>
            {'수정'}
          </Button>
        </div>
      </div>
    </div>
  )
}

export default MyInfo
