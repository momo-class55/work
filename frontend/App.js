import React, { useState, useEffect } from 'react';
import { StatusBar } from 'expo-status-bar';
import { 
  StyleSheet, Text, View, SafeAreaView, TouchableOpacity, 
  Image, ActivityIndicator, TextInput, ScrollView, Alert 
} from 'react-native';

const API_BASE = 'http://localhost:8080/api';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState('LOGIN'); // LOGIN, SIGNUP, USER_HOME, ADMIN_HOME
  const [user, setUser] = useState(null); // { id, name, role, phoneNumber }
  const [loading, setLoading] = useState(false);

  // --- Persistence ---
  useEffect(() => {
    const savedPhone = localStorage.getItem('savedPhone');
    const autoLoginData = localStorage.getItem('autoLoginData');
    
    if (autoLoginData) {
      const { phoneNumber, password } = JSON.parse(autoLoginData);
      handleLogin(phoneNumber, password, true);
    }
  }, []);

  // --- Auth Logic ---
  const handleLogin = async (phoneNumber, password, isAuto = false) => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ phoneNumber, password })
      });
      const data = await response.json();
      if (response.ok) {
        setUser(data);
        setCurrentScreen(data.role === 'ADMIN' ? 'ADMIN_HOME' : 'USER_HOME');
      } else if (!isAuto) {
        Alert.alert('로그인 실패', data.error);
      }
    } catch (e) {
      if (!isAuto) Alert.alert('오류', '서버에 연결할 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleSignup = async (formData) => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/auth/signup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });
      const data = await response.json();
      if (response.ok) {
        Alert.alert('가입 완료', '관리자 승인 후 로그인이 가능합니다.');
        setCurrentScreen('LOGIN');
      } else {
        Alert.alert('가입 실패', data.error);
      }
    } catch (e) {
      Alert.alert('오류', '서버에 연결할 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('autoLoginData');
    setUser(null);
    setCurrentScreen('LOGIN');
  };

  // --- Screen Rendering ---
  return (
    <SafeAreaView style={styles.container}>
      {currentScreen === 'LOGIN' && (
        <LoginScreen 
          onLogin={handleLogin} 
          onGoToSignup={() => setCurrentScreen('SIGNUP')} 
          loading={loading} 
        />
      )}
      {currentScreen === 'SIGNUP' && <SignupScreen onSignup={handleSignup} onBack={() => setCurrentScreen('LOGIN')} loading={loading} />}
      {currentScreen === 'USER_HOME' && <UserHomeScreen user={user} onLogout={handleLogout} />}
      {currentScreen === 'ADMIN_HOME' && <AdminHomeScreen user={user} onLogout={handleLogout} />}
      <StatusBar style="auto" />
    </SafeAreaView>
  );
}

// --- Login Screen ---
const LoginScreen = ({ onLogin, onGoToSignup, loading }) => {
  const [phone, setPhone] = useState('01012345678');
  const [pw, setPw] = useState('password');
  const [autoLogin, setAutoLogin] = useState(false);
  const [saveId, setSaveId] = useState(false);

  useEffect(() => {
    const savedPhone = localStorage.getItem('savedPhone');
    const savedSaveId = localStorage.getItem('saveId') === 'true';
    if (savedPhone) setPhone(savedPhone);
    setSaveId(savedSaveId);
    
    const savedAutoLogin = localStorage.getItem('autoLogin') === 'true';
    setAutoLogin(savedAutoLogin);
  }, []);

  const handleLoginPress = () => {
    if (saveId) {
      localStorage.setItem('savedPhone', phone);
      localStorage.setItem('saveId', 'true');
    } else {
      localStorage.removeItem('savedPhone');
      localStorage.setItem('saveId', 'false');
    }

    if (autoLogin) {
      localStorage.setItem('autoLoginData', JSON.stringify({ phoneNumber: phone, password: pw }));
      localStorage.setItem('autoLogin', 'true');
    } else {
      localStorage.removeItem('autoLoginData');
      localStorage.setItem('autoLogin', 'false');
    }

    onLogin(phone, pw);
  };

  return (
    <View style={styles.authContainer}>
      <Text style={styles.brandTitle}>Shop QR</Text>
      <Text style={styles.brandSubtitle}>프리미엄 식수 관리 시스템</Text>
      
      <View style={styles.inputGroup}>
        <TextInput 
          placeholder="전화번호" 
          style={styles.input} 
          value={phone} 
          onChangeText={setPhone} 
          keyboardType="phone-pad"
        />
        <TextInput 
          placeholder="비밀번호" 
          secureTextEntry 
          style={styles.input} 
          value={pw} 
          onChangeText={setPw} 
        />
      </View>

      <View style={styles.toggleRow}>
        <TouchableOpacity style={styles.checkboxContainer} onPress={() => setSaveId(!saveId)}>
          <View style={[styles.checkbox, saveId && styles.checkboxActive]} />
          <Text style={styles.checkboxLabel}>아이디 저장</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.checkboxContainer} onPress={() => setAutoLogin(!autoLogin)}>
          <View style={[styles.checkbox, autoLogin && styles.checkboxActive]} />
          <Text style={styles.checkboxLabel}>자동 로그인</Text>
        </TouchableOpacity>
      </View>

      <TouchableOpacity style={styles.primaryButton} onPress={handleLoginPress} disabled={loading}>
        {loading ? <ActivityIndicator color="#FFF" /> : <Text style={styles.buttonText}>로그인</Text>}
      </TouchableOpacity>

      <TouchableOpacity onPress={onGoToSignup} style={styles.linkButton}>
        <Text style={styles.linkText}>계정이 없으신가요? 회원가입</Text>
      </TouchableOpacity>
    </View>
  );
};

// --- Signup Screen ---
const SignupScreen = ({ onSignup, onBack, loading }) => {
  const [form, setForm] = useState({ phoneNumber: '', name: '', password: '', passwordConfirm: '', companyName: '' });

  return (
    <ScrollView contentContainerStyle={styles.authContainer}>
      <Text style={styles.screenTitle}>회원가입</Text>
      <View style={styles.inputGroup}>
        <TextInput placeholder="이름" style={styles.input} onChangeText={t => setForm({...form, name: t})} />
        <TextInput placeholder="전화번호" style={styles.input} keyboardType="phone-pad" onChangeText={t => setForm({...form, phoneNumber: t})} />
        <TextInput placeholder="비밀번호" secureTextEntry style={styles.input} onChangeText={t => setForm({...form, password: t})} />
        <TextInput placeholder="비밀번호 확인" secureTextEntry style={styles.input} onChangeText={t => setForm({...form, passwordConfirm: t})} />
        <TextInput placeholder="회사명" style={styles.input} onChangeText={t => setForm({...form, companyName: t})} />
      </View>

      <TouchableOpacity style={styles.primaryButton} onPress={() => onSignup(form)} disabled={loading}>
        <Text style={styles.buttonText}>가입 신청</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={onBack} style={styles.linkButton}>
        <Text style={styles.linkText}>뒤로 가기</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

// --- User Home Screen ---
const UserHomeScreen = ({ user, onLogout }) => {
  const [menu, setMenu] = useState({ content: '불러오는 중...', date: '' });
  const [qrVisible, setQrVisible] = useState(true);
  const [timeLeft, setTimeLeft] = useState(60);
  const [qrTimestamp, setQrTimestamp] = useState(Date.now());

  useEffect(() => {
    fetch(`${API_BASE}/menu/today`)
      .then(r => r.json())
      .then(d => {
        if (d && d.content) {
          setMenu({ content: d.content, date: d.menuDate });
        } else {
          setMenu({ content: '등록된 메뉴가 없습니다.', date: '' });
        }
      })
      .catch(() => setMenu({ content: '메뉴 정보를 가져올 수 없습니다.', date: '' }));
  }, []);

  // QR Timer Logic
  useEffect(() => {
    let timer;
    if (qrVisible && timeLeft > 0) {
      timer = setInterval(() => {
        setTimeLeft(prev => prev - 1);
      }, 1000);
    } else if (timeLeft === 0) {
      setQrVisible(false);
    }
    return () => clearInterval(timer);
  }, [qrVisible, timeLeft]);

  const handleQrToggle = () => {
    if (!qrVisible) {
      setTimeLeft(60); 
      setQrTimestamp(Date.now()); // Update timestamp only when generating new QR
    }
    setQrVisible(!qrVisible);
  };

  return (
    <View style={styles.homeContainer}>
      <View style={styles.header}>
        <View>
          <Text style={styles.welcomeText}>안녕하세요,</Text>
          <Text style={styles.userName}>{user.name} 님</Text>
        </View>
        <TouchableOpacity onPress={onLogout}>
          <Text style={styles.logoutText}>로그아웃</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.card}>
        <View style={styles.cardHeaderRow}>
          <Text style={styles.cardHeader}>오늘의 메뉴</Text>
          {menu.date && <Text style={styles.dateText}>{menu.date}</Text>}
        </View>
        <Text style={styles.menuText}>{menu.content}</Text>
      </View>

      {qrVisible ? (
        <View style={styles.qrModal}>
          <Image 
            source={{ uri: `${API_BASE}/meal/qr-image/${user.userId}?t=${qrTimestamp}` }} 
            style={styles.qrImageLarge}
          />
          <Text style={styles.timerText}>남은 시간: {timeLeft}초</Text>
          <Text style={styles.qrTip}>리더기에 생성된 QR을 스캔해주세요</Text>
        </View>
      ) : (
        <TouchableOpacity style={styles.qrFab} onPress={handleQrToggle}>
          <Text style={styles.qrFabText}>QR 생성</Text>
        </TouchableOpacity>
      )}

      {qrVisible && (
        <TouchableOpacity style={styles.qrCloseButton} onPress={() => setQrVisible(false)}>
          <Text style={styles.qrCloseText}>닫기</Text>
        </TouchableOpacity>
      )}
    </View>
  );
};

// --- Admin Home Screen ---
const AdminHomeScreen = ({ user, onLogout }) => {
  const [stats, setStats] = useState({});
  const [unapproved, setUnapproved] = useState([]);

  const fetchData = async () => {
    try {
      const sRes = await fetch(`${API_BASE}/admin/stats/daily`);
      const sData = await sRes.json();
      setStats(sData);

      const uRes = await fetch(`${API_BASE}/admin/users/unapproved`);
      const uData = await uRes.json();
      setUnapproved(uData);
    } catch (e) {}
  };

  const approveUser = async (id) => {
    await fetch(`${API_BASE}/admin/users/${id}/approve`, { method: 'POST' });
    fetchData();
  };

  useEffect(() => { fetchData(); }, []);

  return (
    <ScrollView style={styles.homeContainer}>
      <View style={styles.header}>
        <Text style={styles.userName}>관리자 대시보드</Text>
        <TouchableOpacity onPress={onLogout}>
          <Text style={styles.logoutText}>로그아웃</Text>
        </TouchableOpacity>
      </View>

      <Text style={styles.sectionTitle}>승인 대기</Text>
      {unapproved.map(u => (
        <View key={u.id} style={styles.listItem}>
          <View>
            <Text style={styles.listText}>{u.name} ({u.phoneNumber})</Text>
            <Text style={styles.listSubtext}>{u.company?.name || '소속 없음'}</Text>
          </View>
          <TouchableOpacity style={styles.smallButton} onPress={() => approveUser(u.id)}>
            <Text style={styles.smallButtonText}>승인</Text>
          </TouchableOpacity>
        </View>
      ))}

      <Text style={styles.sectionTitle}>식수 통계</Text>
      <View style={styles.card}>
        {Object.entries(stats).map(([date, count]) => (
          <View key={date} style={styles.statRow}>
            <Text>{date}</Text>
            <Text style={styles.statCount}>{count} 명</Text>
          </View>
        ))}
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F2F2F7' },
  authContainer: { flex: 1, justifyContent: 'center', padding: 32 },
  brandTitle: { fontSize: 42, fontWeight: '800', textAlign: 'center', marginBottom: 8 },
  brandSubtitle: { fontSize: 18, color: '#8E8E93', textAlign: 'center', marginBottom: 48 },
  screenTitle: { fontSize: 32, fontWeight: '700', marginBottom: 32 },
  inputGroup: { marginBottom: 24 },
  input: { 
    backgroundColor: '#FFF', padding: 18, borderRadius: 12, 
    marginBottom: 12, fontSize: 16, borderWidth: 1, borderColor: '#E5E5EA' 
  },
  primaryButton: { 
    backgroundColor: '#007AFF', padding: 20, borderRadius: 16, 
    alignItems: 'center', shadowColor: '#007AFF', shadowOpacity: 0.3, shadowRadius: 10 
  },
  buttonText: { color: '#FFF', fontSize: 18, fontWeight: '600' },
  linkButton: { marginTop: 24, alignItems: 'center' },
  linkText: { color: '#007AFF', fontSize: 16 },

  toggleRow: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 32 },
  checkboxContainer: { flexDirection: 'row', alignItems: 'center' },
  checkbox: { width: 22, height: 22, borderRadius: 6, borderWidth: 2, borderColor: '#007AFF', marginRight: 10 },
  checkboxActive: { backgroundColor: '#007AFF' },
  checkboxLabel: { fontSize: 15, color: '#8E8E93' },
  
  homeContainer: { flex: 1, padding: 24 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 40 },
  welcomeText: { fontSize: 18, color: '#8E8E93' },
  userName: { fontSize: 28, fontWeight: '700' },
  logoutText: { color: '#FF3B30', fontSize: 16 },
  
  card: { backgroundColor: '#FFF', padding: 24, borderRadius: 24, marginBottom: 24, shadowOpacity: 0.1, shadowRadius: 10, elevation: 2 },
  cardHeaderRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 },
  cardHeader: { fontSize: 16, fontWeight: '600', color: '#8E8E93' },
  dateText: { fontSize: 14, color: '#007AFF', fontWeight: '500' },
  menuText: { fontSize: 20, fontWeight: '600' },
  
  qrFab: { 
    backgroundColor: '#000', padding: 20, borderRadius: 20, 
    alignItems: 'center', position: 'absolute', bottom: 40, left: 24, right: 24 
  },
  qrFabText: { color: '#FFF', fontSize: 18, fontWeight: '700' },
  
  qrModal: { backgroundColor: '#FFF', padding: 32, borderRadius: 32, alignItems: 'center', marginTop: 20, shadowOpacity: 0.1, shadowRadius: 20, elevation: 5 },
  qrImageLarge: { width: 280, height: 280, marginBottom: 10 },
  timerText: { fontSize: 22, fontWeight: '700', color: '#FF3B30', marginBottom: 10 },
  qrTip: { color: '#8E8E93', marginBottom: 20 },
  
  qrCloseButton: { backgroundColor: '#E5E5EA', padding: 16, borderRadius: 16, alignItems: 'center', marginTop: 24 },
  qrCloseText: { color: '#8E8E93', fontSize: 16, fontWeight: '600' },
  
  sectionTitle: { fontSize: 20, fontWeight: '700', marginBottom: 16, marginTop: 8 },
  listItem: { 
    backgroundColor: '#FFF', padding: 20, borderRadius: 16, 
    flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 
  },
  listText: { fontSize: 17, fontWeight: '600' },
  listSubtext: { fontSize: 14, color: '#8E8E93' },
  smallButton: { backgroundColor: '#34C759', paddingHorizontal: 16, paddingVertical: 8, borderRadius: 8 },
  smallButtonText: { color: '#FFF', fontWeight: '600' },
  statRow: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 12 },
  statCount: { fontWeight: '700', color: '#007AFF' }
});
