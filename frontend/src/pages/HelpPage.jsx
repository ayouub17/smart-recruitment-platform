import { HelpCircle, Mail } from 'lucide-react';

const HelpPage = () => {
  return (
    <section className="help-page">
      <HelpCircle size={36} />
      <p className="page-eyebrow">Assistance</p>
      <h1>Comment pouvons-nous vous aider ?</h1>
      <p>Pour toute question sur votre profil, votre CV ou vos candidatures, contactez notre equipe.</p>
      <a className="btn btn-primary" href="mailto:ayoub.exe110@gmail.com">
        <Mail size={16} />Contacter le support
      </a>
    </section>
  );
};

export default HelpPage;
