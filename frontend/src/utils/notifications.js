import { notification } from 'antd';

export const showErrorNotification = (title, message) => {
  notification.error({
    message: title,
    description: message,
    placement: 'topRight',
    duration: 10,
  });
};

export const showSuccessNotification = (title, message) => {
  notification.success({
    message: title,
    description: message,
    placement: 'topRight',
    duration: 10,
  });
};

export const showWarningNotification = (title, message) => {
  notification.warning({
    message: title,
    description: message,
    placement: 'topRight',
    duration: 10,
  });
};

export const showInfoNotification = (title, message) => {
  notification.info({
    message: title,
    description: message,
    placement: 'topRight',
    duration: 10,
  });
};