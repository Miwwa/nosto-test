import React from 'react';
import {useTranslation} from 'react-i18next';
import {Languages} from "./i18n.ts";


interface LanguageSelectProps {
    className?: string;
}

const LanguageSelect: React.FC<LanguageSelectProps> = ({className}) => {
    const {i18n} = useTranslation();

    // Function to handle language change
    const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const selectedLanguage = event.target.value;
        i18n.changeLanguage(selectedLanguage)
            .catch((err) => console.error(err))
    };

    return (
        <select
            className={className}
            value={i18n.language}
            onChange={handleChange}
        >
            {Languages.map((lang) => (
                <option key={lang.code} value={lang.code}>
                    {lang.name}
                </option>
            ))}
        </select>
    );
};

export default LanguageSelect;
