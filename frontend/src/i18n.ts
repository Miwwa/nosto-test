import i18n from 'i18next';
import {initReactI18next} from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

const resources = {
    en: {
        translation: {
            "Currency Exchange": "Currency Exchange",
            "From": "From",
            "To": "To",
            "Amount": "Amount",
            "Convert": "Convert",
            "Error": "Error",
            "Enter amount": "Enter amount",
        },
    },
    ru: {
        translation: {
            "Currency Exchange": "Обмен валют",
            "From": "Из",
            "To": "В",
            "Amount": "Сумма",
            "Convert": "Конвертировать",
            "Error": "Ошибка",
            "Enter amount": "Введите сумму",
        },
    },
};

export const Languages = [
    {code: 'en', name: 'English'},
    {code: 'ru', name: 'Русский'},
]

i18n
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        resources,
        fallbackLng: 'en',
        interpolation: {
            escapeValue: false, // React handles escaping for XSS
        },
    });

export type TranslationResources = typeof resources['en']['translation'];
export type Language = keyof typeof resources;

export default i18n;
