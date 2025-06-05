import api from './index';

// 检查当前登录用户是否有人脸信息
export const checkCurrentUserFace = () => {
  return api.get('/api/face/check');
};

// 通过身份证号检查用户是否有人脸信息
export const checkUserFaceByCard = (card: string) => {
  return api.get(`/api/face/check/${card}`);
};

// 通过人脸验证重置密码
export const resetPasswordByFace = (card: string, image: File, newPassword: string) => {
  const formData = new FormData();
  formData.append('card', card);
  formData.append('image', image);
  formData.append('newPassword', newPassword);

  return api.post('/api/face/reset-password', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

// 保存用户人脸图片
export const saveUserFaceImage = (image: File) => {
  const formData = new FormData();
  formData.append('image', image);

  return api.post('/api/face/save', formData, {
    headers: {
      'Content-Type': 'multipart-form-data'
    }
  });
};