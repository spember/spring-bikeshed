
// @ts-expect-error I don't know what the children are
const Layout = ({children}) => {
    return (
        <>
            <Header />
            {children}
            <Footer />
        </>
    );
}


const Header = () => {
    return (
        <div>
            <h1>Welcome to the BikeShed</h1>
        </div>
    )
}

const Footer = () => {
    return (
        <div>
            <p>Blah blah blah 2024</p>
        </div>
    )
}

export default Layout;